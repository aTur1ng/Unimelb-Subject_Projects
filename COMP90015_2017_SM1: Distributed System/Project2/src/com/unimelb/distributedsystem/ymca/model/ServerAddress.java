package com.unimelb.distributedsystem.ymca.model;

public class ServerAddress
{
	private String	hostname;
	private int		port;

	public ServerAddress(String hostname, int port)
	{
		this.hostname = hostname;
		this.port = port;
	}

	public String getHostname()
	{
		return hostname;
	}

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	@Override
	public int hashCode()
	{
		return hostname.hashCode() * 10 + port;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof ServerAddress)
		{
			ServerAddress addr = (ServerAddress) o;
			return (hostname.equalsIgnoreCase(addr.hostname) && port == addr.port);
		}
		return false;
	}

	@Override
	public String toString()
	{
		return hostname + ":" + port;
	}
}
