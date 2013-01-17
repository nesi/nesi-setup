package grisu.frontend.view.swing.utils.ssh.wizard;

import org.ciscavate.cjwizard.WizardPage;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;

public class FinishPage extends WizardPage implements LogRenderer {
	private JScrollPane scrollPane;
	private JEditorPane editorPane;

	public FinishPage(String title, String description) {
		super(title, description);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getScrollPane(), "2, 2, fill, fill");
		

	}

	@Override
	public void errorWhenLeaving(Exception e) {
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
