package com.unimelb.distributedsystem.ymca.services.dummy;

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

/**
 * 
 * @author alaguipo
 *
 */
public class ServerListServiceDummy implements ServerListService
{
	private Set<ServerAddress> set = new HashSet<ServerAddress>();

	@Override
	public synchronized List<ServerAddress> getServerList(ServerAddress myServer)
	{
		List<ServerAddress> retVal = new ArrayList<ServerAddress>(set);
		if (!set.contains(myServer))
		{
			retVal.add(myServer);
		}
		LoggerUtil.println("Here's the serverList: " + retVal);
		return retVal;
	}

	@Override
	public ServerAddress getRandomServer(ServerAddress myServer)
	{
		List<ServerAddress> addrs = getServerList(myServer);
		if (addrs.size() > 1)
		{
			int ran = new Random().nextInt(addrs.size());
			ServerAddress address = addrs.get(ran);
			LoggerUtil.println("Randomly selected: " + address);
			return address;
		}
		return null;
	}

	@Override
	public void addServers(ServerAddress... addresses) throws EzShareException
	{
		addServers(Arrays.asList(addresses));
	}

	@Override
	public synchronized void addServers(List<ServerAddress> addresses) throws EzShareException
	{
		assertAddress(addresses);
		for (ServerAddress address : addresses)
		{
			set.add(address);
		}
		LoggerUtil.println("Here's the serverList: " + set);
	}

	@Override
	public synchronized void removeServer(ServerAddress address)
	{
		LoggerUtil.println("Removing from server list: " + address);
		set.remove(address);
		LoggerUtil.println("Here's the serverList: " + set);
	}

	private void assertAddress(List<ServerAddress> addresses) throws EzShareException
	{
		if (addresses == null || addresses.size() <= 0)
		{
			throw new EzShareException("missing or invalid server list");
		}
		for (ServerAddress address : addresses)
		{
			assertAddress(address);
		}
	}

	private void assertAddress(ServerAddress address) throws EzShareException
	{
		if (address == null || StringUtil.isEmpty(address.getHostname()) || address.getPort() < 0
				|| address.getPort() > 65535)
		{
			throw new EzShareException("missing or invalid server list");
		}
	}

}
