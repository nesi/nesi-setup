package grisu.frontend.view.swing.utils.ssh.wizard;

import grisu.jcommons.view.html.VelocityUtils;
import grith.jgrith.utils.GridSshKey;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.ctc.wstx.util.StringUtil;
import com.google.common.collect.Maps;

public class MobaXtermIniCreator {

	private final Set<SshBookmark> bookmarks;
	private final String mobaXtermPath = System.getenv("MOBAXTERM_LOCATION");
	private final GridSshKey sshkey;

	public MobaXtermIniCreator(Set<SshBookmark> bookmarks, GridSshKey sshkey) {

		this.sshkey = sshkey;
		this.bookmarks = bookmarks;
		
		try {
			new File(getMobaXtermPath()).mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMobaXtermIniPath() {
		return getMobaXtermPath() +File.separator+"MobaXterm.ini";
	}
	
	public String getMobaXtermPath() {
		if (StringUtils.isNotBlank(mobaXtermPath)) {
		return mobaXtermPath;
		} else {
			String path = System.getenv("APPDATA")+File.separator+"NeSI"+File.separator+"MobaXTerm";
			return path;
		}
	}
	
	public void create() {
				
    	Map<String, Object> properties = Maps.newHashMap();

    	properties.put("bookmarks", bookmarks);
    	String username = System.getProperty("user.name");
    	properties.put("username", username);
    	String homedir = System.getProperty("user.home");
    	properties.put("homedir", homedir);
    	if ( sshkey != null ) {
    		String path = sshkey.getKeyPath();
    		path = path.replace("\\", "/");
    		path = "-i \""+ path + "\"";
    		properties.put("sshkeypath", path);
    	} else {
    		properties.put("sshkeypath", "");
    	}
    	
    	String iniContent = VelocityUtils.render("moba.ini", properties);
    	
    	try {
    		File iniFile = new File(getMobaXtermIniPath());
			FileUtils.writeStringToFile(iniFile, iniContent);
//			File iniMarker = new File(getMobaXtermPath()+File.separator+"MobaXterm.ini.created");
//			FileUtils.writeStringToFile(iniFile, iniContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
