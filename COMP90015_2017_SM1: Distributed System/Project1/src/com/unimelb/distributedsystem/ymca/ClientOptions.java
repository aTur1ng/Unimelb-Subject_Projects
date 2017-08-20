package com.unimelb.distributedsystem.ymca;

import java.net.URI;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Properties;

import com.unimelb.distributedsystem.ymca.consts.Constants;
import com.unimelb.distributedsystem.ymca.model.ServerAddress;
import com.unimelb.distributedsystem.ymca.util.StringUtil;
import com.unimelb.distributedsystem.ymca.util.Util;

public class ClientOptions
{
	private String				channel;
	private String				description;
	private String				host	= Constants.LOCALHOST;
	private String				name;
	private String				owner;
	private String				secret;
	private List<String>		tags;
	private URI					uri;
	private int					port	= Constants.SERVER_PORT;

	private List<ServerAddress>	servers;

	private boolean				query;
	private boolean				remove;
	private boolean				share;
	private boolean				fetch;
	private boolean				publish;
	private boolean				exchange;
	private boolean				debug	= false;
	private boolean				relay;

	public ClientOptions(Properties props)
	{
		channel = Util.getString(props, "channel", channel);
		description = Util.getString(props, "description", description);
		host = Util.getString(props, "host", host);
		name = Util.getString(props, "name", name);
		owner = Util.getString(props, "owner", owner);
		secret = Util.getString(props, "secret", secret);
		tags = Util.getStringList(props, "tags", ",");

		uri = Util.getUri(props, "uri", uri);

		port = Util.getInteger(props, "port", port);
		query = Util.getBoolean(props, "query", query);
		remove = Util.getBoolean(props, "remove", remove);
		share = Util.getBoolean(props, "share", share);
		fetch = Util.getBoolean(props, "fetch", fetch);
		publish = Util.getBoolean(props, "publish", publish);
		debug = Util.getBoolean(props, "debug", debug);
		exchange = Util.getBoolean(props, "exchange", exchange);
		relay = Util.getBoolean(props, "relay", relay);

		servers = Util.getServerList(props, "servers", ",");
		validate();
	}

	public String getChannel()
	{
		return channel;
	}

	public String getDescription()
	{
		return description;
	}

	public String getHost()
	{
		return host;
	}

	public String getName()
	{
		return name;
	}

	public String getOwner()
	{
		return owner;
	}

	public String getSecret()
	{
		return secret;
	}

	public List<String> getTags()
	{
		return tags;
	}

	public URI getUri()
	{
		return uri;
	}

	public int getPort()
	{
		return port;
	}

	public List<ServerAddress> getServers()
	{
		return servers;
	}

	public boolean isQuery()
	{
		return query;
	}

	public boolean isRemove()
	{
		return remove;
	}

	public boolean isShare()
	{
		return share;
	}

	public boolean isFetch()
	{
		return fetch;
	}

	public boolean isPublish()
	{
		return publish;
	}

	public boolean isExchange()
	{
		return exchange;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void validate()
	{
		int commands = 0;
		if (exchange)
			commands++;
		if (publish)
			commands++;
		if (share)
			commands++;
		if (remove)
			commands++;
		if (fetch)
			commands++;
		if (query)
			commands++;
		if (commands >1)
		{
			throw new InvalidParameterException("More than one command detected");
		}
		if (commands == 0)
		{
			throw new InvalidParameterException("invalid command");
		}
		if (uri == null && (publish || share || remove || fetch))
		{
			throw new InvalidParameterException("URI must not be null for command (publish, share, remove or fetch)");
		}
		if (servers == null || servers.size() <= 0 && exchange)
		{
			throw new InvalidParameterException("No server list provided");
		}
	}

	public boolean isRelay()
	{
		return relay;
	}

	public void setRelay(boolean relay)
	{
		this.relay = relay;
	}
}
