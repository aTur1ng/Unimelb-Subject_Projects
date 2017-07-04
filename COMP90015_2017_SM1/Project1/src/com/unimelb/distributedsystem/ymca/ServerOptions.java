package com.unimelb.distributedsystem.ymca;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import com.unimelb.distributedsystem.ymca.consts.Constants;
import com.unimelb.distributedsystem.ymca.util.SecretKeyUtil;

public class ServerOptions
{
	private String	advertiseHostName;
	private int		connectionInterval	= Constants.CONNECTION_INTERVAL;
	private int		exchangeInterval	= Constants.EXCHANGE_INTERVAL;
	private int		port				= Constants.SERVER_PORT;
	private String	secret;
	private boolean	debug				= false;

	public ServerOptions(Properties props)
	{
		if (props.containsKey("advertisedhostname"))
		{
			advertiseHostName = props.getProperty("advertisedhostname");
		}
		else
		{
			try
			{
				advertiseHostName = InetAddress.getLocalHost().getHostName();
			}
			catch (UnknownHostException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (props.containsKey("connectionintervallimit"))
		{
			connectionInterval = Integer.parseInt(props.getProperty("connectionintervallimit"));
		}
		if (props.containsKey("exchangeinterval"))
		{
			exchangeInterval = Integer.parseInt(props.getProperty("exchangeinterval"));
		}
		if (props.containsKey("port"))
		{
			port = Integer.parseInt(props.getProperty("port"));
		}
		if (props.containsKey("secret"))
		{
			secret = props.getProperty("secret");
			SecretKeyUtil.setKey(secret);
		}
		else
		{
			secret = SecretKeyUtil.generateKey();
		}
		if (props.containsKey("debug"))
		{
			debug = Boolean.parseBoolean(props.getProperty("debug"));
		}
	}

	public String getAdvertiseHostName()
	{
		return advertiseHostName;
	}

	public int getConnectionInterval()
	{
		return connectionInterval;
	}

	public int getExchangeInterval()
	{
		return exchangeInterval;
	}

	public int getPort()
	{
		return port;
	}

	public String getSecret()
	{
		return secret;
	}

	public boolean isDebug()
	{
		return debug;
	}

}
