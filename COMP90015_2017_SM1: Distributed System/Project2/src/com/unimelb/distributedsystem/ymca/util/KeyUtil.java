package com.unimelb.distributedsystem.ymca.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class KeyUtil
{
	private static SecureRandom	random	= new SecureRandom();
	private static String		secret	= new BigInteger(130, random).toString(32);

	public static String generateKey()
	{
		KeyUtil.secret = new BigInteger(130, random).toString(32);
		return KeyUtil.secret;
	}

	public static void setKey(String secret)
	{
		KeyUtil.secret = secret;
	}

	public static String getKey()
	{
		return KeyUtil.secret;
	}

	public static String getId()
	{
		return new BigInteger(130, random).toString(32);
	}

}
