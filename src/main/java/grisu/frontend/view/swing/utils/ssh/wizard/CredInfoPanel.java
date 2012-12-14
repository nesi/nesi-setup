package grisu.frontend.view.swing.utils.ssh.wizard;

import grisu.jcommons.view.html.VelocityUtils;
import grith.jgrith.cred.Cred;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.google.common.collect.Maps;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import grisu.frontend.view.swing.utils.ssh.SshKeyPanel;

public class CredInfoPanel extends JPanel {
	private JLabel lblDetails;
	private JScrollPane scrollPane;
	private JEditorPane infoPane;
	
	private Cred credential;
	private SshKeyPanel sshKeyPanel;

	/**
	 * Create the panel.
	 */
	public CredInfoPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getLblDetails(), "2, 2");
		add(getScrollPane(), "2, 4, fill, fill");
		add(getSshKeyPanel(), "2, 6, fill, fill");
		setCredential(null);

	}

	private JLabel getLblDetails() {
		if (lblDetails == null) {
			lblDetails = new JLabel("Details");
		}
		return lblDetails;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getEditorPane());
		}
		return scrollPane;
	}
	private JEditorPane getEditorPane() {
		if (infoPane == null) {
			infoPane = new JEditorPane();
			infoPane.setEditable(false);
			infoPane.setContentType("text/html");
		}
		return infoPane;
	}
	
	public void setCredential(Cred c) {
		
		credential = c;
		
		if ( credential == null ) {
			try {
				URL url = getClass().getResource("/notLoggedIn.html");
				infoPane.setPage(url);
			} catch (IOException e) {

			}
		} else {
		
    	Map<String, Object> properties = Maps.newHashMap();
    	properties.put("dn", credential.getDN());
    	properties.put("cn", credential.getDN());
    	
    	String html = VelocityUtils.render("credInfoPanel", properties);
    	getEditorPane().setText(html);
		}
	}

	public void setLoginError(Exception e) {

		Map<String, Object> properties = Maps.newHashMap();
		properties.put("error_msg", e.getLocalizedMessage());
		String html = VelocityUtils.render("loginError", properties);
		getEditorPane().setText(html);
		
	}

	public void setLoginStarted() {
		
		getEditorPane().setText("");
		
	}
	private SshKeyPanel getSshKeyPanel() {
		if (sshKeyPanel == null) {
			sshKeyPanel = new SshKeyPanel();
		}
		return sshKeyPanel;
	}
}
