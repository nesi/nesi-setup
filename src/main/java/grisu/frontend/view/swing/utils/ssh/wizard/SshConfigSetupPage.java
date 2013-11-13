package grisu.frontend.view.swing.utils.ssh.wizard;

import grisu.control.ServiceInterface;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import org.ciscavate.cjwizard.WizardPage;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class SshConfigSetupPage extends WizardPage {
	
	private JScrollPane scrollPane;
	private JEditorPane editorPane;
	private JCheckBox chckbxCreateConfigFor;
	private JCheckBox chckbxNewCheckBox;
	
	public SshConfigSetupPage(String title, String description) {
		super(title, description);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(47dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		add(getScrollPane(), "2, 2, 3, 1, fill, fill");
		add(getMobaCheckBox(), "4, 6");
		add(getSshConfigCheckBox(), "4, 8");

	}

	@Override
	public void errorWhenLeaving(Exception ex) {

		final String msg = ex.getLocalizedMessage();
		final ErrorInfo info = new ErrorInfo("Error creating config file",
				"Error creating config file.", msg,
				"Error", ex,
				Level.SEVERE, null);

		final JXErrorPane pane = new JXErrorPane();
		pane.setErrorInfo(info);

		JXErrorPane.showDialog(SshConfigSetupPage.this, pane);

	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getEditorPane());
		}
		return scrollPane;
	}
	private JEditorPane getEditorPane() {
		if (editorPane == null) {
			editorPane = new JEditorPane();
			editorPane.setEditable(false);
			editorPane.setContentType("text/html");
			URL url = getClass().getResource("/sshconfig.html");
			try {
				editorPane.setPage(url);
			} catch (IOException e) {

			}
		}
		return editorPane;
	}
	
	public boolean createMobaXTermConfig() {
		return getMobaCheckBox().isSelected();
	}
	
	public boolean createSshConfig() {
		return getSshConfigCheckBox().isSelected();
	}
	
	
	public void setServiceInterface(ServiceInterface si) {

	}


	private JCheckBox getMobaCheckBox() {
		if (chckbxCreateConfigFor == null) {
			chckbxCreateConfigFor = new JCheckBox("Create config for MobaXTerm (only available for Windows)");
			
			String currentOs = System.getProperty("os.name").toUpperCase();
			if (! currentOs.contains("WINDOWS") ) {
				chckbxCreateConfigFor.setEnabled(false);
			} else { 
				chckbxCreateConfigFor.setSelected(true);
			}

		}
		return chckbxCreateConfigFor;
	}
	private JCheckBox getSshConfigCheckBox() {
		if (chckbxNewCheckBox == null) {
			chckbxNewCheckBox = new JCheckBox("Add entries to .ssh/config");
			String currentOs = System.getProperty("os.name").toUpperCase();
			if ( currentOs.contains("WINDOWS") ) {
				chckbxNewCheckBox.setSelected(false);
			} else { 
				chckbxNewCheckBox.setSelected(true);
			}
		}
		return chckbxNewCheckBox;
	}
}
