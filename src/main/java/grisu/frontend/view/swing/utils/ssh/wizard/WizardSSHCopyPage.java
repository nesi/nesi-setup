package grisu.frontend.view.swing.utils.ssh.wizard;

import grisu.control.ServiceInterface;
import grisu.jcommons.view.html.VelocityUtils;
import grith.jgrith.utils.GridSshKey;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.ciscavate.cjwizard.WizardPage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class WizardSSHCopyPage extends WizardPage {
	
	private JScrollPane scrollPane;
	private JEditorPane editorPane;
	private JScrollPane scrollPane_1;
	private JPanel panel;
	private AdvancedSSHOptionsPanel advancedSSHOptionsPanel;
	private JCheckBox chckbxSkipThisStep;
	private SshTargetSelectionPanel sshTargetSelectionPanel;
		
	public WizardSSHCopyPage(String title, String description) {
		super(title, description);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(47dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(43dlu;default):grow"),}));
		add(getScrollPane(), "2, 2, 3, 1, fill, fill");
//		add(getChckbxSkipThisStep(), "2, 4, 3, 1");
		add(getSshTargetSelectionPanel(), "4, 4, fill, fill");
		add(getScrollPane_1(), "4, 6, fill, fill");

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
			editorPane.setContentType("text/html");
			URL url = getClass().getResource("/ssh.html");
			try {
				editorPane.setPage(url);
			} catch (IOException e) {

			}
		}
		return editorPane;
	}
	
	private void setSshKey(String ssh_key_path, String ssh_cert_path, String id) {
    	Map<String, Object> properties = Maps.newHashMap();
    	properties.put("key_path", ssh_key_path);
    	properties.put("cert_path", ssh_cert_path);
    	properties.put("ssh_id", id);
    	
    	String html = VelocityUtils.render("sshDetails", properties);
    	getEditorPane().setText(html);
	}
	private JScrollPane getScrollPane_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setViewportView(getPanel());
		}
		return scrollPane_1;
	}
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new FormLayout(new ColumnSpec[] {
					ColumnSpec.decode("default:grow"),},
				new RowSpec[] {
					RowSpec.decode("default:grow"),}));
			panel.add(getAdvancedSSHOptionsPanel(), "1, 1, fill, fill");
		}
		return panel;
	}
	private AdvancedSSHOptionsPanel getAdvancedSSHOptionsPanel() {
		if (advancedSSHOptionsPanel == null) {
			advancedSSHOptionsPanel = new AdvancedSSHOptionsPanel();
		}
		return advancedSSHOptionsPanel;
	}

	public GridSshKey getGridSshKey() {
		return getAdvancedSSHOptionsPanel().getGridSshKey();
	}
//	private JCheckBox getChckbxSkipThisStep() {
//		if (chckbxSkipThisStep == null) {
//			chckbxSkipThisStep = new JCheckBox("Skip this step");
//			chckbxSkipThisStep.addItemListener(new ItemListener() {
//				public void itemStateChanged(ItemEvent arg0) {
//					
//					if (arg0.getStateChange() == ItemEvent.DESELECTED) {
////						setNextEnabled(true);
////						setFinishEnabled(false);
//						getAdvancedSSHOptionsPanel().lockUI(false);
//						getSshTargetSelectionPanel().lockUI(false);
//					} else {
////						setNextEnabled(false);
////						setFinishEnabled(true);
//						getAdvancedSSHOptionsPanel().lockUI(true);
//						getSshTargetSelectionPanel().lockUI(true);
//					}
//					
//				}
//			});
//		}
//		return chckbxSkipThisStep;
//	}
//	
	public Set<String> getSelectedSites() {
		return getSshTargetSelectionPanel().getSelectedSites();
	}
	
	private SshTargetSelectionPanel getSshTargetSelectionPanel() {
		if (sshTargetSelectionPanel == null) {
			sshTargetSelectionPanel = new SshTargetSelectionPanel();
		}
		return sshTargetSelectionPanel;
	}
	
	public void setServiceInterface(ServiceInterface si) {
		getSshTargetSelectionPanel().setServiceInterface(si);
	}

	public boolean isForceCreateNewKey() {
		return getAdvancedSSHOptionsPanel().isForceCreateNewKey();
	}

//	public boolean isSkipSshStep() {
//		return getChckbxSkipThisStep().isSelected();
//	}
}
