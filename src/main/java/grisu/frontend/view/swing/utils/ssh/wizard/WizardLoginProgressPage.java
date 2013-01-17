package grisu.frontend.view.swing.utils.ssh.wizard;

import grisu.jcommons.view.html.VelocityUtils;
import grith.jgrith.cred.Cred;
import grith.jgrith.utils.GridSshKey;

import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.ciscavate.cjwizard.WizardPage;

import com.google.common.collect.Maps;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class WizardLoginProgressPage extends WizardPage implements LogRenderer {
	private JLabel lblProgress;
	private JProgressBar progressBar;
	private JTextField txtNotLoggedIn;
	private JScrollPane scrollPane;
	private JEditorPane editorPane;

	public WizardLoginProgressPage(String title, String description) {
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
				RowSpec.decode("max(72dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getLblProgress(), "2, 2, right, default");
		add(getLoggingInStatus(), "4, 2, fill, default");
		add(getProgressBar(), "4, 4, fill, default");
		add(getScrollPane(), "4, 6, fill, fill");
	}

	@Override
	public void errorWhenLeaving(Exception e) {
		// TODO Auto-generated method stub

	}
	
	public void setLoginFinished() {
		
		SwingUtilities.invokeLater(new Thread() {
			public void run() {

					getLoggingInStatus().setText("Logged in");
					setLoginProcessPercentage(100);
			}
		});
		
	}
	
	public void setLoginProcessPercentage(int percent) {
		getProgressBar().setIndeterminate(false);
		getProgressBar().setValue(percent);
		
	}
	private JLabel getLblProgress() {
		if (lblProgress == null) {
			lblProgress = new JLabel("Status");
		}
		return lblProgress;
	}
	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
		}
		return progressBar;
	}
	private JTextField getLoggingInStatus() {
		if (txtNotLoggedIn == null) {
			txtNotLoggedIn = new JTextField();
			txtNotLoggedIn.setEditable(false);
			txtNotLoggedIn.setHorizontalAlignment(SwingConstants.CENTER);
			txtNotLoggedIn.setColumns(10);
		}
		return txtNotLoggedIn;
	}

	public void setLoginStarted(final String idp, final String username) {
		
		SwingUtilities.invokeLater(new Thread() {
			public void run() {
				getLoggingInStatus().setText("Logging in...");
				getProgressBar().setIndeterminate(true);
				addMessage("Login to '"+idp+"'...");
				addMessage("Username: "+username);
			}
		});

		
	}

	public void setLoginError(final Exception e) {

		SwingUtilities.invokeLater(new Thread() {
			public void run() {
				getLoggingInStatus().setText("Logging error");
				getProgressBar().setIndeterminate(false);
				getProgressBar().setValue(0);
				
				addMessage("Login error: "+e.getLocalizedMessage());
			}
		});
		
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
	
	public void addMessage(final String msg) {
		
		SwingUtilities.invokeLater(new Thread() {
			public void run() {
				String current = getEditorPane().getText();
				getEditorPane().setText(current+"\n"+msg);				
			}
		});

		
	}

	@Override
	public void clearProgressLog() {
		getEditorPane().setText("");
		
	}




}
