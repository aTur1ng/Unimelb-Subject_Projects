package com.unimelb.distributedsystem.ymca.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.unimelb.distributedsystem.ymca.exceptions.EzShareException;
import com.unimelb.distributedsystem.ymca.model.ServerAddress;
import com.unimelb.distributedsystem.ymca.services.ServerListService;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;
import com.unimelb.distributedsystem.ymca.util.StringUtil;

//TODO: Chen
/**
 * Handle server list.
 * 
 * @author alaguipo
 *
 */
public class ServerListServiceImpl implements ServerListService
{

	/**
	 * Return list of server including own server
	 */
	@Override
	public synchronized List<ServerAddress> getServerList(ServerAddress myServer)
	{
		return null;
	}

	/**
	 * Return 1 random server excluding self
	 */
	@Override
	public ServerAddress getRandomServer(ServerAddress myServer)
	{
		return null;
	}

	/**
	 * Add an array of server addresses
	 */
	@Override
	public void addServers(ServerAddress... addresses) throws EzShareException
	{
	}

	/**
	 * Add a list of server addresses
	 */
	@Override
	public synchronized void addServers(List<ServerAddress> addresses) throws EzShareException
	{
	}

	/**
	 * Remove one server address from the list
	 */
	@Override
	public synchronized void removeServer(ServerAddress address)
	{
	}

}
