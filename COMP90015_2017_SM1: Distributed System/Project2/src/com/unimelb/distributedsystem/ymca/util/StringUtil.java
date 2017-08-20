package com.unimelb.distributedsystem.ymca.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StringUtil
{
	public static boolean isEmpty(String empty)
	{
		return empty == null || empty.length() <= 0;
	}

	public static boolean isUrl(URI uri)
	{
		boolean retVal = false;
		try
		{
			URL url = uri.toURL();
			if (uri.getAuthority() == null || url.toString().startsWith("file:/"))
			{
				retVal = false;
			}
			else
			{
				retVal = true;
			}
		}
		catch (MalformedURLException e)
		{
		}
		LoggerUtil.println(uri + " is URL? " + retVal);
		return retVal;
	}

	public static boolean isFileAndExists(URI uri)
	{
		boolean retVal = false;
		try
		{
			Path path = Paths.get(uri);
			boolean exists = path.toFile().exists();
			retVal = exists;
		}
		catch (Exception e)
		{
		}
		LoggerUtil.println(uri + " is file and exists? " + retVal);
		return retVal;
	}
}
