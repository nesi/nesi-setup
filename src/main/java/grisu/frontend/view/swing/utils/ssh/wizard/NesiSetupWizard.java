package grisu.frontend.view.swing.utils.ssh.wizard;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.frontend.control.login.LoginManager;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.MountPoint;
import grisu.model.UserEnvironmentManager;
import grisu.model.dto.DtoMountPoints;
import grisu.model.dto.GridFile;
import grisu.model.info.dto.Directory;
import grisu.model.info.dto.Queue;
import grith.jgrith.cred.AbstractCred.PROPERTY;
import grith.jgrith.cred.Cred;
import grith.jgrith.cred.SLCSCred;
import grith.jgrith.utils.GridSshKey;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ciscavate.cjwizard.WizardContainer;
import org.ciscavate.cjwizard.WizardListener;
import org.ciscavate.cjwizard.WizardPage;
import org.ciscavate.cjwizard.WizardSettings;
import org.ciscavate.cjwizard.pagetemplates.PageTemplate;
import org.ciscavate.cjwizard.pagetemplates.TitledPageTemplate;
import org.python.modules.synchronize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class NesiSetupWizard extends JFrame implements WizardListener,
		PropertyChangeListener {

	public static final ImmutableSet<String> SITES = ImmutableSet.of(
			"auckland", "canterbury");

	public static final ImmutableSet<String> HOSTS = ImmutableSet
			.of("pan.nesi.org.nz");

	public static final Logger myLogger = LoggerFactory
			.getLogger(NesiSetupWizard.class);

	public static final String NO_FILE = "NO_FILE";

	public static final String GRISU_BACKEND = "bestgrid";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		LoginManager.initEnvironment();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					NesiSetupWizard frame = new NesiSetupWizard();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JPanel contentPane;

	private final WizardContainer wizard;

	private final NesiSetupWizardPageFactory pageFactory;
	private Cred credential;
	private ServiceInterface si = null;
	private FileManager fm = null;

	private UserEnvironmentManager uem = null;
	private GridSshKey sshkey;
	private boolean forceCreateNewKey = false;

	private Set<String> sites;

	private Map<PROPERTY, Object> credconfig;
	private final Map<String, File> sshAuthFilesMap = Maps.newConcurrentMap();

	private final Map<String, String> sshUsernameMap = Maps.newConcurrentMap();

	private List<MountPoint> mountpoints = null;

	/**
	 * Create the frame.
	 */
	public NesiSetupWizard() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 680, 507);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		pageFactory = new NesiSetupWizardPageFactory();
		PageTemplate pt = new TitledPageTemplate();
		wizard = new WizardContainer(pageFactory, pt);
		wizard.setNextEnabled(true);
		wizard.addWizardListener(this);
		getContentPane().add(wizard);
	}

	private void copySshKey(WizardSSHCopyProgressPage progressPage,
			boolean forceCreateNewKey) throws Exception {

		progressPage.setLoginStarted();

		try {

			if (sshkey.exists() && !forceCreateNewKey) {
				progressPage.addMessage("Using existing ssh key '"
						+ sshkey.getKeyPath() + "'");
			} else {
				if (sshkey.exists() && forceCreateNewKey) {
					progressPage.addMessage("Overwriting existing ssh key '"
							+ sshkey.getKeyPath() + "'");
					FileUtils.deleteQuietly(new File(sshkey.getCertPath()));
					FileUtils.deleteQuietly(new File(sshkey.getKeyPath()));
				} else {
					progressPage.addMessage("Creating new ssh key: "
							+ sshkey.getKeyPath());
				}
				try {
					if (sshkey.getPassword() == null
							|| sshkey.getPassword().length == 0) {
						sshkey.setPassword((char[]) credconfig
								.get(PROPERTY.Password));
					}
					sshkey.createIfNecessary();

				} catch (Exception e) {
					progressPage.addMessage("Could not create ssh key: "
							+ e.getLocalizedMessage());
					throw e;
				}
			}

			String publicKey = sshkey.getCertPath();

			String pubContent = FileUtils.readFileToString(new File(publicKey));
			progressPage.addMessage("\n\t" + pubContent + "\n");

			for (String site : sites) {
				copyToSite(site, progressPage);
			}

		} finally {
			progressPage.setLoginFinished();
		}

	}

	private void copyToSite(String site, LogRenderer r) {

		Set<Directory> dirs = getDirectoriesForSite(site);

		Set<String> urls = new HashSet<String>();
		for (Directory d : dirs) {
			urls.add(d.toUrl());
		}

		for (String url : urls) {
			boolean useUrl = false;
			for (String allowed_host : HOSTS) {
				if (url.contains(allowed_host)) {
					useUrl = true;
					break;
				}
			}
			if (useUrl) {
				ensureKeyIsPresent(url, r);
			}

		}

	}

	private void ensureKeyIsPresent(String baseUrl, LogRenderer r) {

		r.addMessage("FileSystem: " + baseUrl);
		File file = getSshAuthorizedKeysForDirectory(baseUrl);
		String content;
		try {
			content = FileUtils.readFileToString(file);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		String pubKey;
		try {
			pubKey = FileUtils.readFileToString(new File(sshkey.getCertPath()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (content.contains(pubKey)) {
			r.addMessage("\tKey already present");
		} else {
			r.addMessage("\tAdding public key...");
			try {
				FileUtils.writeStringToFile(file, pubKey, true);

				fm.uploadFile(file, baseUrl + ".ssh/authorized_keys", true);
				r.addMessage("\tauthorized_keys file updated.");
			} catch (Exception e) {
				r.addMessage("\tCould not update/upload authorized_keys file: "
						+ e.getLocalizedMessage());
			}
		}

	}

	private Set<Directory> getDirectoriesForSite(String site) {
		Set<Directory> dirs = Sets.newHashSet();

		for (Queue q : uem.getAllAvailableSubmissionLocations()) {
			if (!site.equalsIgnoreCase((q.getGateway().getSite().getName()))) {
				continue;
			}

			for (Directory d : q.getDirectories()) {
				dirs.add(d);
			}
		}

		return dirs;
	}

	private synchronized List<MountPoint> getMountPoints() {
		if (mountpoints == null) {
			DtoMountPoints temp = si.df();
			if (temp == null || temp.getMountpoints().size() == 0) {
				throw new RuntimeException("No filesystems available for user.");
			}
			mountpoints = temp.getMountpoints();
		}
		return mountpoints;
	}

	private File getSshAuthorizedKeysForDirectory(String baseUrl) {

		synchronized (baseUrl.intern()) {

			File authFile = sshAuthFilesMap.get(baseUrl);
			if (authFile == null) {
				try {
					System.out.println("URL: " + baseUrl);
					authFile = fm
							.downloadFile(baseUrl + ".ssh/authorized_keys");
					sshAuthFilesMap.put(baseUrl, authFile);
					System.out.println("CONTENT " + baseUrl + ": " + authFile);
				} catch (FileTransactionException e) {
					myLogger.debug("Can't access " + baseUrl
							+ ".ssh/authorized_keys: "
							+ e.getLocalizedMessage());
					throw new RuntimeException(e);
				}

			}
			return authFile;
		}

	}

	private String getUsernameForUrl(String url) {

		synchronized (url.intern()) {
			String username = sshUsernameMap.get(url);

			if (StringUtils.isBlank(username)) {

				GridFile file = null;
				try {
					file = fm.ls(url);
				} catch (RemoteFileSystemException e) {
					myLogger.debug("Can't access " + url + ": "
							+ e.getLocalizedMessage());
					throw new RuntimeException(e);
				}

				String tmp = FileManager.removeTrailingSlash(file.getUrl());
				username = tmp.substring(tmp.lastIndexOf("/") + 1);
				sshUsernameMap.put(url, username);
				System.out.println("Username: " + username);
			}
			return username;
		}
	}

	private void login(WizardLoginProgressPage infoPage,
			Map<PROPERTY, Object> config) {

		if (si != null) {
			wizard.setNextEnabled(true);
			return;
		}

		try {

			String idp = (String) config.get(PROPERTY.IdP);
			String un = (String) config.get(PROPERTY.Username);

			infoPage.setLoginStarted(idp, un);

			credential = new SLCSCred();

			credential.init(config);

			infoPage.addMessage("Logging in successful.");

			infoPage.addMessage("Connecting to compute infrastructure...");
			si = LoginManager.login(GRISU_BACKEND, credential, false);
			fm = GrisuRegistryManager.getDefault(si).getFileManager();
			uem = GrisuRegistryManager.getDefault(si)
					.getUserEnvironmentManager();
			Thread t = new Thread() {
				public void run() {
					preload();
				}
			};
			t.setName("getAllSshAuthFiles");
			t.start();
			infoPage.addMessage("Success!");
			setServiceInterface(si);
			infoPage.setLoginFinished();
			wizard.setNextEnabled(true);
		} catch (Exception e) {
			infoPage.setLoginError(e);
		}
	}

	@Override
	public void onCanceled(List<WizardPage> path, WizardSettings settings) {

		System.out.println("cancelled");
	}

	@Override
	public void onFinished(List<WizardPage> path, WizardSettings settings) {

		System.out.println("finished");
	}

	@Override
	public void onPageAboutToChange(final WizardPage oldPage,
			final List<WizardPage> path) throws Exception {
		// login
		if (oldPage instanceof WizardLoginPage) {

			wizard.setNextEnabled(false);
			WizardLoginPage page = (WizardLoginPage) oldPage;
			credconfig = page.getCredentialConfig();

		} else if (oldPage instanceof WizardSSHCopyPage) {
			wizard.setNextEnabled(false);
			final WizardSSHCopyPage sshcopyPage = (WizardSSHCopyPage) oldPage;
			sshkey = sshcopyPage.getGridSshKey();
			sites = sshcopyPage.getSelectedSites();
			forceCreateNewKey = sshcopyPage.isForceCreateNewKey();

		}
	}

	@Override
	public void onPageChanged(final WizardPage newPage, List<WizardPage> path) {

		if (newPage instanceof WizardLoginProgressPage) {

			Thread t = new Thread() {
				public void run() {

					login((WizardLoginProgressPage) newPage, credconfig);
				}
			};
			t.setName("LoginThread");
			t.start();

		}

		if (newPage instanceof WizardSSHCopyProgressPage) {

			final WizardSSHCopyProgressPage page = (WizardSSHCopyProgressPage) newPage;

			page.clearProgressLog();
			wizard.setPrevEnabled(false);
			wizard.setFinishEnabled(false);

			Thread t = new Thread() {
				public void run() {
					try {
						copySshKey(page, forceCreateNewKey);
						wizard.setNextEnabled(true);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						wizard.setPrevEnabled(true);
						wizard.setFinishEnabled(true);
					}
				}
			};
			t.setName("SshCopyThread");
			t.start();
		}

	}

	private void preload() {

		for (String site : uem.getAllAvailableSites()) {
			preloadForSite(site);
		}

	}

	private void preloadForSite(String site) {

		final Set<Directory> dirs = getDirectoriesForSite(site);

		for (final Directory d : dirs) {
			Thread t = new Thread() {
				public void run() {
					try {
						getSshAuthorizedKeysForDirectory(d.toUrl());
					} catch (Exception e) {
						myLogger.error("Error preloading environment.", e);
					}
				}
			};
			t.setName("Lookup: " + d.toUrl());
			t.start();
			Thread t2 = new Thread() {
				public void run() {
					try {
						getUsernameForUrl(d.toUrl());
					} catch (Exception e) {
						myLogger.error("Error preloading username.", e);
					}
				}
			};
			t2.setName("UsernameLookup: " + d.toUrl());
			t2.start();
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {

		System.out.println("Event: " + arg0.getPropertyName());
		System.out.println("Value: " + arg0.getNewValue());

	}

	public void setServiceInterface(final ServiceInterface si) {

		Thread t = new Thread() {
			public void run() {
				WizardSSHCopyPage page = (WizardSSHCopyPage) pageFactory
						.getPage(NesiSetupWizardPageFactory.SSH_COPY_NAME);
				page.setServiceInterface(si);

			}
		};

		t.setName("InitPages");
		t.start();

	}

}
