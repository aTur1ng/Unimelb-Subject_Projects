package com.unimelb.distributedsystem.ymca.services;

import java.util.List;

import com.unimelb.distributedsystem.ymca.exceptions.EzShareException;
import com.unimelb.distributedsystem.ymca.model.ServerAddress;

//TODO: Implement by Chen
public interface ServerListService
{
	/**
	 * Get all servers stored
	 * 
	 * @return
	 */
	public List<ServerAddress> getServerList(ServerAddress myServer);

	/**
	 * Return a random server
	 * 
	 * @return
	 */
	public ServerAddress getRandomServer(ServerAddress myServer);

	/**
	 * Add server
	 * 
	 * @param addresses
	 */
	public void addServers(ServerAddress... addresses) throws EzShareException;

	/**
	 * Add servers
	 * 
	 * @param addresses
	 */
	public void addServers(List<ServerAddress> addresses) throws EzShareException;

	/**
	 * Remove an address
	 * 
	 * @param address
	 */
	public void removeServer(ServerAddress address);
}
