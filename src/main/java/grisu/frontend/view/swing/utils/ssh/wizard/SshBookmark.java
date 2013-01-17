package grisu.frontend.view.swing.utils.ssh.wizard;

import com.google.common.base.Objects;

import grisu.jcommons.configuration.CommonGridProperties;

public class SshBookmark implements Comparable<SshBookmark> {
	
	private String name;
	private String host;
	private String username;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public int hashCode() {
		return Objects.hashCode(getName());
	}
	
	public boolean equals(Object other) {
		if (! (other instanceof SshBookmark)) {
			return false;
		}
		SshBookmark o = (SshBookmark)other;
		return getName().equals(o.getName());
	}
	
	@Override
	public int compareTo(SshBookmark o) {
		return getName().compareTo(o.getName());
	}

}
