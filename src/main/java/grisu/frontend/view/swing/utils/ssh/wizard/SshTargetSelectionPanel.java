package grisu.frontend.view.swing.utils.ssh.wizard;

import grisu.control.ServiceInterface;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class SshTargetSelectionPanel extends JPanel {
	
	public static final Logger myLogger = LoggerFactory.getLogger(SshTargetSelectionPanel.class);
	
	private JCheckBox chckbxAuckland;
	private JCheckBox chckbxChristchurch;
	
	private ServiceInterface si = null;
	private UserEnvironmentManager uem = null;
	
	private Set<String> selectedSites = Sets.newTreeSet();


	private TitledBorder border = new TitledBorder(null, "Please select sites to access via ssh", TitledBorder.LEADING, TitledBorder.TOP, null, null);

	/**
	 * Create the panel.
	 */
	public SshTargetSelectionPanel() {
		setBorder(border);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		for ( final String site : NesiSetupWizard.SITES ) {
			
//			if ( ! NesiSetupWizard.SITES.contains(site.toLowerCase()) ) {
//				myLogger.debug("Not offering "+site+" because it is not in the set of allowed sites.");
//				continue;
//			}
			final String tempsite = StringUtils.capitalize(site);
			
			final JCheckBox temp = new JCheckBox(tempsite);
			temp.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					
					if ( arg0.getStateChange() == ItemEvent.DESELECTED ) {
						selectedSites.remove(site);
					} else {
						selectedSites.add(site);
					}
					
				}
			});
			
			temp.setSelected(true);
			
//			if (dn.toLowerCase().contains(site.toLowerCase())) {
//				temp.setSelected(true);
//				selectedSites.add(site);
//			}

			add(temp);
				
		}

	
	}
	
	public void lockUI(final boolean lock) {
		
		SwingUtilities.invokeLater(new Thread() {public void run(){
			SshTargetSelectionPanel.this.setEnabled(!lock);
			for ( Component c : getComponents()) {
				c.setEnabled(!lock);
			}
		} 
		});
		
	}
	
	public void setServiceInterface(ServiceInterface si) {
		
		this.si = si;
		String dn = si.getDN();
		uem = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager();
		
		
	}

	public Set<String> getSelectedSites() {
		return selectedSites;
	}

}
