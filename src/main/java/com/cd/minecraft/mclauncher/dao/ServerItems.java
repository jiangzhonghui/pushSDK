package com.cd.minecraft.mclauncher.dao;

public class ServerItems {
	
	private long id;
	private String serverName;
	private String serverDesc;
	private String serverauthor;
	private String serverIp;
	private String serverPort;
	private String serverIcon;
	private String peVersion;
	private String serverType;
	
	
	public String getPeVersion() {
		return peVersion;
	}
	public void setPeVersion(String peVersion) {
		this.peVersion = peVersion;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerDesc() {
		return serverDesc;
	}
	public void setServerDesc(String serverDesc) {
		this.serverDesc = serverDesc;
	}
	public String getServerauthor() {
		return serverauthor;
	}
	public void setServerauthor(String serverauthor) {
		this.serverauthor = serverauthor;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	public String getServerIcon() {
		return serverIcon;
	}
	public void setServerIcon(String serverIcon) {
		this.serverIcon = serverIcon;
	}
	
	
}
