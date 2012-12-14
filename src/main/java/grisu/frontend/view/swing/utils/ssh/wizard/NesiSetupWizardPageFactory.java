package grisu.frontend.view.swing.utils.ssh.wizard;

import java.util.List;
import java.util.Map;

import org.ciscavate.cjwizard.PageFactory;
import org.ciscavate.cjwizard.WizardPage;
import org.ciscavate.cjwizard.WizardSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class NesiSetupWizardPageFactory implements PageFactory {
	
	Logger myLogger = LoggerFactory.getLogger(NesiSetupWizardPageFactory.class);
	
	public static final String LOGIN_PAGE_NAME = "Login";
	public static final String LOGIN_INFO_NAME = "Login Progress";
	public static final String SSH_COPY_NAME = "SSH copy";
	public static final String SSH_COPY_PROGRESS_NAME = "SSH copy Progress";
    
    private Map<String, WizardPage> pages = Maps.newLinkedHashMap();
    
    public NesiSetupWizardPageFactory() {
    	
    	WizardPage loginPage = new WizardLoginPage(LOGIN_PAGE_NAME, "Logging into institution account");
    	pages.put(LOGIN_PAGE_NAME, loginPage);
    	
    	WizardLoginProgressPage infoPage = new WizardLoginProgressPage(LOGIN_INFO_NAME, "Progress");
    	pages.put(LOGIN_INFO_NAME, infoPage);
    	
    	WizardSSHCopyPage sshPage = new WizardSSHCopyPage(SSH_COPY_NAME, "Copying ssh public keys");
    	pages.put(SSH_COPY_NAME, sshPage);
    	
    	WizardSSHCopyProgressPage sshProgressPage = new WizardSSHCopyProgressPage(SSH_COPY_PROGRESS_NAME, "Progress");
    	pages.put(SSH_COPY_PROGRESS_NAME, sshProgressPage);
    	
    }
    
    private String getTitleForIndex(int index) {
    	
    	return Iterables.get(pages.keySet(), index);
    	
    }
    
    public WizardPage getPage(String name) {
    	
    	return pages.get(name);
    }
	

	@Override
	public WizardPage createPage(List<WizardPage> path, WizardSettings settings) {
		
		return pages.get(getTitleForIndex(path.size()));
		
	}



}
