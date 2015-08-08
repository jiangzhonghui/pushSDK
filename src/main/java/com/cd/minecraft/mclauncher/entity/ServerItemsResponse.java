package com.cd.minecraft.mclauncher.entity;

import java.util.List;

import com.cd.minecraft.mclauncher.dao.ServerItems;

public class ServerItemsResponse {
	
	List<ServerItems> servers;

	public List<ServerItems> getServers() {
		return servers;
	}

	public void setServers(List<ServerItems> servers) {
		this.servers = servers;
	}
	

}
