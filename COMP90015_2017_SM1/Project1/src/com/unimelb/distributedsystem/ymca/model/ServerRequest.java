package com.unimelb.distributedsystem.ymca.model;

import java.security.InvalidParameterException;
import java.util.List;

public class ServerRequest
{
	public enum Command
	{
		EXCHANGE, FETCH, QUERY, PUBLISH, SHARE, REMOVE;
	}

	private Command				command;
	private String				secret;
	private Boolean				relay;
	private Resource			resource;
	private ResourceTemplate	resourceTemplate;
	private List<ServerAddress>	serverList;

	public Command getComm()
	{
		return command;
	}

	public String getCommand()
	{
		return command.name();
	}

	public void setCommand(Command command)
	{
		this.command = command;
	}

	public void setCommand(String command)
	{
		for (Command comm : Command.values())
		{
			if (comm.name().equals(command))
			{
				this.command = comm;
				return;
			}
		}
		throw new InvalidParameterException("Invalid command: " + command);
	}

	public boolean isRelay()
	{
		return relay == null ? false : relay;
	}

	public void setRelay(boolean relay)
	{
		this.relay = relay;
	}

	public Resource getResource()
	{
		return resource;
	}

	public void setResource(Resource resource)
	{
		this.resource = resource;
	}

	public ResourceTemplate getResourceTemplate()
	{
		return resourceTemplate;
	}

	public void setResourceTemplate(ResourceTemplate resourceTemplate)
	{
		this.resourceTemplate = resourceTemplate;
	}

	public String getSecret()
	{
		return secret;
	}

	public void setSecret(String secret)
	{
		this.secret = secret;
	}

	public List<ServerAddress> getServerList()
	{
		return serverList;
	}

	public void setServerList(List<ServerAddress> serverList)
	{
		this.serverList = serverList;
	}
}
