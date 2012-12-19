package grisu.frontend.view.swing.utils.ssh.wizard;

import grisu.jcommons.configuration.CommonGridProperties;
import grith.jgrith.utils.GridSshKey;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.logging.Level;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SshKeyPanel extends JPanel implements ActionListener {
	
	public static final Logger myLogger = LoggerFactory.getLogger(SshKeyPanel.class);
	
	private final static String NEW_KEY = "new key";
	private final static String NEW_KEY_IDP = "new key idp";
	private final static String EXISTING_KEY = "existing key";
	
	
	private JLabel lblPassword;
	private JPasswordField passwordField;
	
	private JRadioButton newKey;
	private JRadioButton existingKey;
	private JTextField textField;
	private JButton btnBrowse;
	private JRadioButton newKeyIdp;
	
	private final ButtonGroup group = new ButtonGroup();
	
	private final GridSshKey sshkey;
	
	private String lastCommand = null;
	

	/**
	 * Create the panel.
	 */
	public SshKeyPanel() {
		
		try {
			sshkey = GridSshKey.getDefaultGridsshkey();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(9dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getNewKeyIdp(), "2, 2, 7, 1");
		add(getNewKey(), "2, 4, 7, 1");
		add(getLblPassword(), "4, 6, right, default");
		add(getPasswordField(), "6, 6, 3, 1, fill, default");
		add(getExistingKey(), "2, 8, 7, 1");
		add(getTextField(), "4, 10, 3, 1, fill, default");
		add(getBtnBrowse(), "8, 10, right, default");
		init();
	}
	
	private void init() {
		
		if ( sshkey.exists() ) {
			getExistingKey().setSelected(true);
			getTextField().setText(sshkey.getKeyPath());
			setSshKeyType(EXISTING_KEY);
		} else {
			getNewKeyIdp().setSelected(true);
			setSshKeyType(NEW_KEY_IDP);
		}
		
		
	}
	
	public void lockUI(final boolean lock) {
		
		SwingUtilities.invokeLater(new Thread() {public void run(){
			for ( Component c : getComponents()) {
				c.setEnabled(!lock);
			}
		} 
		});
	}

	private JLabel getLblPassword() {
		if (lblPassword == null) {
			lblPassword = new JLabel("Password:");
			lblPassword.setEnabled(false);
		}
		return lblPassword;
	}

	private JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
			passwordField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					sshkey.setPassword(passwordField.getPassword());
				}
			});
			passwordField.setEnabled(false);
		}
		return passwordField;
	}


	
	public GridSshKey getGridSshKey() {
		return sshkey;
	}
	
	public String getGridSshKeyPath() {

		return sshkey.getKeyPath();
		
	}
	
	public String getGridSshCertPath() {
		return sshkey.getCertPath();
				
	}
	
	public String getGridSshId() {
		return sshkey.getId();
	}
	
	public char[] getSshKeyPassphrase() {
		return sshkey.getPassword();
	}

	public void setId(String id) {
		sshkey.setId(id);
	}


	public void setSshKeyPasspharse(char[] passphrase) {
		sshkey.setPassword(passphrase);
	}

	public void setSshKeyPath(String sshKeyPath) {
		sshkey.setKeyPath(sshKeyPath);
	}
	private JRadioButton getNewKey() {
		if (newKey == null) {
			newKey = new JRadioButton("Create new ssh key");
			group.add(newKey);
			newKey.addActionListener(this);
			newKey.setActionCommand(NEW_KEY);
		}
		return newKey;
	}
	private JRadioButton getExistingKey() {
		if (existingKey == null) {
			existingKey = new JRadioButton("Use existing ssh key");
			group.add(existingKey);
			existingKey.addActionListener(this);
			existingKey.setActionCommand(EXISTING_KEY);
		}
		return existingKey;
	}
	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setEnabled(false);
			textField.setColumns(10);
		}
		return textField;
	}
	private JButton getBtnBrowse() {
		if (btnBrowse == null) {
			btnBrowse = new JButton("Browse");
			btnBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					

					final JFileChooser fc = new JFileChooser();

					int returnVal = fc.showOpenDialog(SshKeyPanel.this);
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			            fc.setFileHidingEnabled(false);
			            
			            if ( ! file.exists() ) {
							final ErrorInfo info = new ErrorInfo("SSH error",
									"Not valid ssh key.", "ssh private key does not exist: "+file.getAbsolutePath(),
									"Error", null,
									Level.WARNING, null);

							final JXErrorPane pane = new JXErrorPane();
							pane.setErrorInfo(info);

							JXErrorPane.showDialog(SshKeyPanel.this, pane);
			            	return;
			            } else if ( ! new File(file.getAbsoluteFile()+CommonGridProperties.CERT_EXTENSION).exists() ) {
							final ErrorInfo info = new ErrorInfo("SSH error",
									"Not valid ssh key.", "ssh public key does not exist: "+file.getAbsolutePath()+CommonGridProperties.CERT_EXTENSION,
									"Error", null,
									Level.WARNING, null);

							final JXErrorPane pane = new JXErrorPane();
							pane.setErrorInfo(info);

							JXErrorPane.showDialog(SshKeyPanel.this, pane);
			            	return;
			            } else {
			            	getTextField().setText(file.getAbsolutePath());
			            	setSshKeyType(EXISTING_KEY);
			            }
			        } else {
			            return;
			        }
				}
			});
			btnBrowse.setEnabled(false);
		}
		return btnBrowse;
	}
	private JRadioButton getNewKeyIdp() {
		if (newKeyIdp == null) {
			newKeyIdp = new JRadioButton("Create new ssh key (using institution password)");
			group.add(newKeyIdp);
			newKeyIdp.addActionListener(this);
			newKeyIdp.setActionCommand(NEW_KEY_IDP);
		}
		return newKeyIdp;
	}
	
	private void setSshKeyType(String command) {
		if ( EXISTING_KEY.equals(command) ) {
			getTextField().setEnabled(true);
			getPasswordField().setEnabled(false);
			getBtnBrowse().setEnabled(true);
			sshkey.setKeyPath(getTextField().getText());
		} else if ( NEW_KEY.equals(command) ) {
			getTextField().setEnabled(false);
			getPasswordField().setEnabled(true);
			getBtnBrowse().setEnabled(false);
			sshkey.setKeyPath(CommonGridProperties.GRID_KEY_PATH);
		} else if ( NEW_KEY_IDP.equals(command)) {
			getTextField().setEnabled(false);
			getPasswordField().setEnabled(false);
			getBtnBrowse().setEnabled(false);
			sshkey.setKeyPath(CommonGridProperties.GRID_KEY_PATH);
		}
		lastCommand = command;
				
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		String command = arg0.getActionCommand();
		setSshKeyType(command);

	}

	public boolean isForceCreateNewKey() {

		if ( EXISTING_KEY.equals(lastCommand) ) {
			return false;
		}
		
		if ( new File(CommonGridProperties.getDefault().getGridSSHKey()).exists() ) {
			
		    JOptionPane pane = new JOptionPane(
		            "ssh key already exists at: "+CommonGridProperties.getDefault().getGridSSHKey()+"\nOverwrite?");
		        Object[] options = new String[] { "Overwrite", "Use existing" };
		        pane.setOptions(options);
		        Point loc = this.getLocation();
		        JDialog dialog = pane.createDialog(new JFrame(), "Dilaog");
		        dialog.setLocation(new Point((int)loc.getX(), (int)loc.getY() + 250));
		        dialog.setVisible(true);
		        Object obj = pane.getValue(); 
		        int result = -1;
		        for (int k = 0; k < options.length; k++)
		          if (options[k].equals(obj))
		            result = k;
		        return 0 == result;

		} else {
			return false;
		}
	}
}
