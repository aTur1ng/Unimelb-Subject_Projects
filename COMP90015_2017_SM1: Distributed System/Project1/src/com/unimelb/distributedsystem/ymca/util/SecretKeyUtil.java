package com.unimelb.distributedsystem.ymca.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class SecretKeyUtil
{
	private static SecureRandom	random	= new SecureRandom();
	private static String		secret	= new BigInteger(130, random).toString(32);

	public static String generateKey()
	{
		SecretKeyUtil.secret = new BigInteger(130, random).toString(32);
		return SecretKeyUtil.secret;
	}

	public static void setKey(String secret)
	{
		SecretKeyUtil.secret = secret;
	}

	public static String getKey()
	{
		return SecretKeyUtil.secret;
	}
}
