//package com.unimelb.distributedsystem.ymca;
//
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import com.unimelb.distributedsystem.ymca.tcp.Hook;
//
//@Ignore
//public class IntegrationTest
//{
//	String	server1Settings	= "-debug -port 50001 -connectionintervallimit 0";
//	String	server2Settings	= "-debug -port 50002 -connectionintervallimit 0";
//	String	server3Settings	= "-debug -port 50003 -connectionintervallimit 0";
//
//	@Before
//	public void setup() throws InterruptedException
//	{
//		Hook.enable();
//		Thread.sleep(1000);
//		// create 3 servers and 2 clients for testing
//		new ServerRunner(server1Settings).start();
////		new ServerRunner(server2Settings).start();
////		new ServerRunner(server3Settings).start();
//	}
//
//	@Before
//	public void tearDown()
//	{
//		// shut down servers
//		Hook.terminate();
//	}
//
//	@Test
//	public void testPublishQueryRemove()
//	{
//		String publishArg = "-debug -publish -name Google -description Description -uri http://www.google.com -port 50001";
//		Client.main(args(publishArg));
//
//		String queryArg = "-debug -query -uri http://www.google.com -port 50001";
//		Client.main(args(queryArg));
//
//		String removeArg = "-debug -remove -uri http://www.google.com -port 50001";
//		Client.main(args(removeArg));
//		
//		try
//		{
//			Thread.sleep(60000);
//		}
//		catch (InterruptedException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void testShareFetchRemove()
//	{
//
//	}
//
//	@Test
//	public void testExchange()
//	{
//
//	}
//
//	private static String[] args(String args)
//	{
//		return args.split(" ");
//	}
//
//	private static class ServerRunner extends Thread
//	{
//		String ar;
//
//		public ServerRunner(String ar)
//		{
//			this.ar = ar;
//		}
//
//		@Override
//		public void run()
//		{
//			Server.main(args(ar));
//		}
//	}
//
//}
