package grisu.frontend.view.swing.utils.ssh.wizard;

import grith.gridsession.view.SLCSCredPanel;
import grith.jgrith.cred.AbstractCred.PROPERTY;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import org.ciscavate.cjwizard.WizardPage;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WizardLoginPage extends WizardPage  {
	
	public static final Logger myLogger = LoggerFactory.getLogger(WizardLoginPage.class);
	
	private SLCSCredPanel credPanel;
	private JScrollPane scrollPane;
	private JEditorPane infoPane;

	public WizardLoginPage(String title, String description) {
		super(title, description);
		
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getScrollPane(), "2, 2, fill, fill");
		add(getCredPanel(), "2, 4, fill, fill");
	}


	private SLCSCredPanel getCredPanel() {
		if (credPanel == null) {
			credPanel = new SLCSCredPanel();
		}
		return credPanel;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getInfoPane());
		}
		return scrollPane;
	}
	private JEditorPane getInfoPane() {
		if (infoPane == null) {
			infoPane = new JEditorPane();
			infoPane.setEditable(false);
			infoPane.setContentType("text/html");
			URL url = getClass().getResource("/login.html");
			try {
				infoPane.setPage(url);
			} catch (IOException e) {

			}
		}
		return infoPane;
	}


	

	public Map<PROPERTY, Object> getCredentialConfig() {
		return getCredPanel().createCredConfig();
	}


	@Override
	public void errorWhenLeaving(Exception ex) {

		final String msg = ex.getLocalizedMessage();
		final ErrorInfo info = new ErrorInfo("Login error",
				"Login failed: "+ex.getLocalizedMessage(), msg,
				"Error", ex,
				Level.SEVERE, null);

		final JXErrorPane pane = new JXErrorPane();
		pane.setErrorInfo(info);

		JXErrorPane.showDialog(WizardLoginPage.this, pane);
		
	}
}
