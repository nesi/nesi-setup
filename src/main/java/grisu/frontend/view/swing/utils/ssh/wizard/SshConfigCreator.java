package grisu.frontend.view.swing.utils.ssh.wizard;

import grisu.jcommons.configuration.CommonGridProperties;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.python.antlr.PythonParser.continue_stmt_return;

import com.ctc.wstx.util.StringUtil;

public class SshConfigCreator {
	
	private final Set<SshBookmark> bookmarks;
	private final String sshConfigPath = CommonGridProperties.SSH_DIR+File.separator+"config";

	
	public SshConfigCreator(Set<SshBookmark> bookmarks) {
		
		this.bookmarks = bookmarks;
		
	}
	
	public String getSshConfigPath() {
		return sshConfigPath;
	}
	
	public String addEntries(String sshkeypath) throws IOException {
		
		StringBuffer log = new StringBuffer();
		
		String oldConfig;
		try {
			oldConfig = FileUtils.readFileToString(new File(getSshConfigPath()));
		} catch (IOException e) {
			oldConfig = "";
		}

		StringBuffer config = new StringBuffer();
		
		log.append("Creating entries for following hosts to ssh config file: "+getSshConfigPath()+":\n\n");

		boolean addedEntry = false;
		for ( SshBookmark b : bookmarks ) {
			
			if ( oldConfig.contains(b.getHost()) ) {
				log.append("\tconfig file already contains host: "+b.getHost()+", skipping...\n\n");
				continue;
			}
			
			String alias = b.getName().replace("(", "_"); 
			alias = alias.replace(")", "_"); 
			alias = alias.replace(" ", "_"); 
			
			alias = alias.toLowerCase();
			
			config.append("\nHost "+alias+"\n");
			config.append("\nHostName "+b.getHost()+"\n");
			config.append("User " + b.getUsername() + "\n");
			
			log.append("\t"+b.getHost()+" (alias: "+alias+", username: "+b.getUsername()+")");
			addedEntry = true;
			if ( StringUtils.isNotBlank(sshkeypath) ) {
				config.append("IdentityFile = \""
					+ sshkeypath + "\"\n\n");
			}

		}
		
		if (!addedEntry) {
			log.append("\tNo hosts added");
		} else {
			log.append("\n\nYou can log into those hosts using the ssh commandline:\n\n\tssh <alias>");
		}
		
		boolean append = false;
		if ( new File(getSshConfigPath()).exists() ) {
			append = true;
		}
		FileUtils.writeStringToFile(new File(getSshConfigPath()), config.toString(), append);

		return log.toString();
	}

}
