package grisu.frontend.view.swing.utils.ssh.wizard;

import com.google.common.base.Objects;

import grith.jgrith.utils.GridSshKey;

public class SshBookmark implements Comparable<SshBookmark> {
	
	private String name;
	private String host;
	private String username;
	
	@Override
	public int compareTo(SshBookmark o) {
		return getName().compareTo(o.getName());
	}
	public boolean equals(Object other) {
		if (! (other instanceof SshBookmark)) {
			return false;
		}
		SshBookmark o = (SshBookmark)other;
		return getName().equals(o.getName());
	}
	public String getHost() {
		return host;
	}
	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}
	public int hashCode() {
		return Objects.hashCode(getName());
	}
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	

	public void setUsername(String username) {
		this.username = username;
	}

}
