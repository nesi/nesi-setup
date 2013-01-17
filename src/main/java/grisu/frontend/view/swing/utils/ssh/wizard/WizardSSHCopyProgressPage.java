package grisu.frontend.view.swing.utils.ssh.wizard;

import org.ciscavate.cjwizard.WizardPage;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class WizardSSHCopyProgressPage extends WizardPage implements LogRenderer {
	
	private JScrollPane scrollPane;
	private JEditorPane editorPane;
	private JLabel lblStatus;
	private JTextField textField;
	private JProgressBar progressBar;

	public WizardSSHCopyProgressPage(String title, String description) {
		super(title, description);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		add(getLblStatus(), "2, 2, right, default");
		add(getTextField(), "4, 2, fill, default");
		add(getProgressBar(), "4, 4");
		add(getScrollPane(), "2, 6, 3, 1, fill, fill");
	}

	@Override
	public void errorWhenLeaving(Exception e) {
		// TODO Auto-generated method stub

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
		}
		return editorPane;
	}
	
	public void clearProgressLog() {
		getEditorPane().setText("");
	}

	public void addMessage(final String msg) {
		
		SwingUtilities.invokeLater(new Thread() {
			public void run() {
				String current = getEditorPane().getText();
				getEditorPane().setText(current+"\n"+msg);				
			}
		});

		
	}
	
	public void setLoginStarted() {
		
		SwingUtilities.invokeLater(new Thread() {
			public void run() {
				getTextField().setText("Copying...");
				getProgressBar().setIndeterminate(true);
			}
		});

		
	}	
	
	public void setLoginFinished() {
		
		SwingUtilities.invokeLater(new Thread() {
			public void run() {

					getTextField().setText("Copying done");
					getProgressBar().setIndeterminate(false);
					getProgressBar().setValue(100);

				

			}
		});
		
	}
	
	private JLabel getLblStatus() {
		if (lblStatus == null) {
			lblStatus = new JLabel("Status:");
		}
		return lblStatus;
	}
	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setHorizontalAlignment(SwingConstants.CENTER);
			textField.setEditable(false);
			textField.setColumns(10);
		}
		return textField;
	}
	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
		}
		return progressBar;
	}
}
