package grisu.frontend.view.swing.utils.ssh.wizard;

import org.ciscavate.cjwizard.WizardPage;

public class FinishPage extends WizardPage {

	public FinishPage(String title, String description) {
		super(title, description);
	}

	@Override
	public void errorWhenLeaving(Exception e) {
	}

}
