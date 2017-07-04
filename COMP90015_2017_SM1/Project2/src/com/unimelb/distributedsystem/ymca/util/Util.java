package com.unimelb.distributedsystem.ymca.util;

import java.io.File;
import java.net.URI;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unimelb.distributedsystem.ymca.model.ServerAddress;
import com.unimelb.distributedsystem.ymca.model.ServerRequest;

public class Util
{

	/**
	 * Parses command line to properties file
	 * 
	 * @param args
	 *            Command line arguments
	 * @return Properties with mapped values
	 */
	public static Properties generatePropertiesFromArguments(String[] args)
	{
		Properties props = new Properties();
		for (int i = 0; i < args.length; i++)
		{
			String arg = args[i].trim();
			if (arg.startsWith("-"))
			{
				String key = arg.substring(1);
				if (i < args.length - 1 && !args[i + 1].startsWith("-"))
				{
					props.setProperty(key, args[i + 1]);
					i++;
				}
				else
				{
					props.setProperty(key, "true");
				}
			}
			else
			{
				throw new InvalidParameterException("Invalid parameter: " + arg);
			}
		}
		return props;
	}

	public static <T> String toJson(T obj)
	{
		return new Gson().toJson(obj);
	}

	public static <T> String toPrettyJson(T obj)
	{
		return new GsonBuilder().setPrettyPrinting().create().toJson(obj);
	}

	public static <T> T fromJson(String jsonString, Class<T> cls)
	{
		return new Gson().fromJson(jsonString, cls);
	}

	public static String getString(Properties props, String key, String defaultValue)
	{
		String val = props.getProperty(key);
		if (null == val)
		{
			return defaultValue;
		}
		else
		{
			return val;
		}
	}

	public static URI getUri(Properties props, String key, URI defaultValue)
	{
		URI retVal = defaultValue;
		String val = props.getProperty(key);
		if (null == val)
		{
			retVal = defaultValue;
		}
		else
		{
			try
			{
				retVal = URI.create(val);
			}
			catch (Throwable t)
			{
				File f = new File(val);
				LoggerUtil.println(f.toURI().toString());
				retVal = f.toURI();
			}
		}
		if (retVal != null)
		{
			LoggerUtil.println(val + "->" + retVal.toString());
		}
		return retVal;
	}

	public static Integer getInteger(Properties props, String key, Integer defaultValue)
	{
		String val = props.getProperty(key);
		if (null == val)
		{
			return defaultValue;
		}
		else
		{
			return Integer.parseInt(val);
		}
	}

	public static Boolean getBoolean(Properties props, String key, Boolean defaultValue)
	{
		String val = props.getProperty(key);
		if (null == val)
		{
			return defaultValue;
		}
		else
		{
			return Boolean.parseBoolean(val);
		}
	}

	public static List<String> getStringList(Properties props, String key, String separator)
	{
		String val = props.getProperty(key);
		if (null == val)
		{
			return new ArrayList<String>();
		}
		else
		{
			return Arrays.asList(val.split(separator));
		}
	}

	public static List<ServerAddress> getServerList(Properties props, String key, String separator)
	{
		List<ServerAddress> retVal = new ArrayList<ServerAddress>();
		List<String> split = getStringList(props, key, separator);
		if (split != null && split.size() > 0)
		{
			for (String host : split)
			{
				String[] splitHost = host.split(":");
				if (splitHost.length != 2)
				{
					throw new InvalidParameterException("Invalid Inet Address: " + host);
				}
				try
				{
					int port = Integer.parseInt(splitHost[1]);
					retVal.add(new ServerAddress(splitHost[0], port));
				}
				catch (Exception e)
				{
					throw new InvalidParameterException("Invalid Inet Address: " + host);
				}
			}
		}
		return retVal;
	}

	public static boolean checkListNotNull(@SuppressWarnings("rawtypes") List list){
		return (null!=list && !list.isEmpty());
	}
	
}
