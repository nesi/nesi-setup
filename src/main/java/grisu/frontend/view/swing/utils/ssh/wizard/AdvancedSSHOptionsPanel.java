package grisu.frontend.view.swing.utils.ssh.wizard;

import grith.jgrith.utils.GridSshKey;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class AdvancedSSHOptionsPanel extends JPanel {

	public static final Logger myLogger = LoggerFactory
			.getLogger(AdvancedSSHOptionsPanel.class);

	private JCheckBox chckbxAdvancedConnectionSettings;
	private SshKeyPanel sshKeyPanel;

	/**
	 * Create the panel.
	 */
	public AdvancedSSHOptionsPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("33px:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("22px"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		add(getChckbxAdvancedConnectionSettings(), "1, 2, 2, 1, left, center");
		add(getSshKeyPanel(), "2, 4, fill, fill");

	}

	public void enableAdvancedOptions(boolean enable) {
		getChckbxAdvancedConnectionSettings().setEnabled(enable);
//		getSshKeyPanel().enableSshKeyCreation(enable);
	}

	public static void ensureGridSshKeyExists(String keyPath, String certPath, String id, char[] password) throws Exception {

		

	}

	private JCheckBox getChckbxAdvancedConnectionSettings() {
		if (chckbxAdvancedConnectionSettings == null) {
			chckbxAdvancedConnectionSettings = new JCheckBox(
					"Advanced ssh options");
			chckbxAdvancedConnectionSettings
					.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {

							showAdvancedOptions(chckbxAdvancedConnectionSettings
									.isSelected());

						}
					});
		}
		return chckbxAdvancedConnectionSettings;
	}


	

	private SshKeyPanel getSshKeyPanel() {
		if (sshKeyPanel == null) {
			sshKeyPanel = new SshKeyPanel();
			sshKeyPanel.setVisible(false);
		}
		return sshKeyPanel;
	}

	public void lockUI(final boolean lock) {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getChckbxAdvancedConnectionSettings().setEnabled(!lock);
				getSshKeyPanel().lockUI(lock);

			}
		});

	}
	


	private void showAdvancedOptions(boolean show) {

		getSshKeyPanel().setVisible(show);

	}

	public GridSshKey getGridSshKey() {
		return getSshKeyPanel().getGridSshKey();
	}

	public boolean isForceCreateNewKey() {
		return getSshKeyPanel().isForceCreateNewKey();
	}

}
