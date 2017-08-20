package com.unimelb.distributedsystem.ymca.util;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerUtil
{
	private static Logger log;

	private static boolean enabled;

	public static void setEnabled(Logger logger,boolean enabled)
	{   
		LoggerUtil.log = logger;
		PropertyConfigurator.configure(LoggerUtil.class.getClassLoader().getResourceAsStream("log4j.properties"));
		LoggerUtil.enabled = enabled;
		if(enabled)
			log.info("setting debug on");
		LogManager.getRootLogger().setLevel(Level.DEBUG);
	}

	public static void println(Object s)
	{
		print(log,s);
	}
	
	public static void printInfo(Object s)
	{
		printInfo(log,s);
	}

	public static void println(Object... s)
	{
		for(Object s1:s){
			print(log,s1);
		}
	}

	public static void printInfo(Logger logger,Object s)
	{
			logger.info(s);
	}
	
	public static void print(Logger logger,Object s)
	{
		if (enabled)
		{
			logger.debug(s);
		}
	}

	public static void print(Object s,Throwable t)
	{
		print(log,s,t);
	}

	public static void print(Logger logger,Object s,Throwable t)
	{
		if (enabled)
		{
			logger.debug(s,t);
		}
	}
}
