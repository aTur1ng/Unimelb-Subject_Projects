package com.unimelb.distributedsystem.ymca.model;

import java.net.URI;
import java.util.List;

public class Resource implements Cloneable
{
	private String			name;
	private String			description;
	private List<String>	tags;
	private URI				uri;
	private String			channel	= "";
	private String			owner	= "";
	private String			ezServer;
	private Integer			resourceSize;

	public Resource(String channel, String owner, URI uri)
	{
		this.channel = channel == null ? "" : sanitize(channel);
		this.owner = owner == null ? "" : sanitize(owner);
		this.uri = uri;
	}

	@Override
	public int hashCode()
	{
		int hashCode = 0;
		if (owner != null)
			hashCode += owner.hashCode() * 1_000_000;
		if (channel != null)
			hashCode += channel.hashCode() * 1_000;
		if (uri != null)
			hashCode += uri.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Resource)
		{
			Resource r = (Resource) o;
			return owner.equalsIgnoreCase(r.owner) && channel.equalsIgnoreCase(r.channel) && uri.compareTo(r.uri) == 0;
		}
		return false;
	}

	@Override
	public Resource clone()
	{
		try
		{
			Resource cloned = (Resource) super.clone();
			cloned.setOwner("*");
			return cloned;
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			return this;
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = sanitize(name);
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = sanitize(description);
	}

	public List<String> getTags()
	{
		return tags;
	}

	public void setTags(List<String> tags)
	{
		this.tags = tags;
	}

	public URI getUri()
	{
		return uri;
	}

	public void setUri(URI uri)
	{
		this.uri = uri;
	}

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String channel)
	{
		this.channel = channel == null ? "" : sanitize(channel);
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner == null ? "" : sanitize(owner);
	}

	public String getEzServer()
	{
		return ezServer;
	}

	public void setEzServer(String ezServer)
	{
		this.ezServer = sanitize(ezServer);
	}

	public Integer getResourceSize()
	{
		return resourceSize;
	}

	public void setResourceSize(Integer resourceSize)
	{
		this.resourceSize = resourceSize;
	}

	private String sanitize(String string)
	{
		if (string == null)
		{
			return null;
		}
		return string.trim().replaceAll("\0", "");
	}
}
