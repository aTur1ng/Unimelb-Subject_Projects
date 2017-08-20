package com.unimelb.distributedsystem.ymca.model;

import java.net.URI;

public class ResourceTemplate extends Resource
{

	public ResourceTemplate(URI uri)
	{
		super("", "", uri);
	}

	public ResourceTemplate(String channel, String owner, URI uri)
	{
		super(channel, owner, uri);
	}

}
