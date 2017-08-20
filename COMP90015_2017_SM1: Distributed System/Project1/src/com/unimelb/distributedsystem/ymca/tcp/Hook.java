package com.unimelb.distributedsystem.ymca.tcp;

public class Hook
{
	private static boolean terminate = false;

	public static boolean isTerminate()
	{
		return terminate;
	}

	public static void terminate()
	{
		terminate = false;
	}

	public static void enable()
	{
		terminate = false;
	}
}
