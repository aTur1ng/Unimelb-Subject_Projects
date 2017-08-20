package com.unimelb.distributedsystem.ymca.services.dummy;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unimelb.distributedsystem.ymca.model.Resource;
import com.unimelb.distributedsystem.ymca.model.ResourceTemplate;
import com.unimelb.distributedsystem.ymca.model.ResultSize;
import com.unimelb.distributedsystem.ymca.tcp.Hook;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;
import com.unimelb.distributedsystem.ymca.util.SocketUtil;
import com.unimelb.distributedsystem.ymca.util.Util;

public class SubscriptionData
{
	private Map<String, SubscriptionClass> subscriptionMap;

	public SubscriptionData()
	{
		subscriptionMap = new HashMap<String, SubscriptionClass>();
	}

	public void subscribe(String id, Socket socket, ResourceTemplate template, List<Socket> outSocket)
	{
		SubscriptionClass subs = subscriptionMap.get(id);
		if (subs != null && subs.getSocket() != null && !subs.getSocket().isClosed())
		{
			LoggerUtil.println("Socket for id=" + id + " already exists. Closing socket.");
			try
			{
				subs.getSocket().close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<SubscriptionExchange> outInterChange = new ArrayList<SubscriptionExchange>();
		for (Socket outS : outSocket)
		{
			SubscriptionExchange ex = new SubscriptionExchange(socket, outS);
			outInterChange.add(ex);
		}
		SubscriptionClass cls = new SubscriptionClass(socket, template, outInterChange);
		subscriptionMap.put(id, cls);
		cls.startRelayThreads();
		LoggerUtil.println("Subscription id=" + id + "; template=" + Util.toJson(template));
	}

	public void unsubscribe(String id)
	{
		SubscriptionClass subs = subscriptionMap.remove(id);
		if (subs != null && subs.getSocket() != null && !subs.getSocket().isClosed())
		{
			try
			{
				ResultSize resSize = new ResultSize(subs.getCounter());
				SocketUtil.send(subs.getSocket().getOutputStream(), resSize);
				subs.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void compareAndSend(Resource resource) throws IOException
	{
		for (String key : subscriptionMap.keySet())
		{
			SubscriptionClass cls = subscriptionMap.get(key);
			if (cls == null || cls.getSocket() == null || cls.getSocket().isClosed())
			{
				LoggerUtil.println("Subscription id=" + key + " doesn't exist.");
				subscriptionMap.remove(key);
				return;
			}
			if (ResourceData.queryCompare(resource, cls.getTemplate()))
			{
				cls.addCounter();
				LoggerUtil.println("Sending resource= " + Util.toJson(resource) + "; to ID=" + key);
				SocketUtil.send(cls.getSocket().getOutputStream(), resource);
			}
			else
			{
				LoggerUtil.println("Template doesn't match to subscription id=" + key);
			}
		}
	}

	public static class SubscriptionClass
	{
		private Socket						socket;
		private ResourceTemplate			template;
		private List<SubscriptionExchange>	outSocket;
		private int							counter;

		public SubscriptionClass(Socket socket, ResourceTemplate template, List<SubscriptionExchange> outSocket)
		{
			this.socket = socket;
			this.template = template;
			this.outSocket = outSocket;
		}

		public Socket getSocket()
		{
			return socket;
		}

		public ResourceTemplate getTemplate()
		{
			return template;
		}

		public List<SubscriptionExchange> getOutSockets()
		{
			return outSocket;
		}

		public void close()
		{
			if (socket != null && !socket.isClosed())
			{
				try
				{
					socket.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (outSocket != null && outSocket.size() > 0)
			{
				for (SubscriptionExchange ex : outSocket)
				{
					ex.close();
				}
			}
		}

		public void startRelayThreads()
		{
			LoggerUtil.println("Starting relay threads");
			if (outSocket != null && outSocket.size() > 0)
			{
				for (SubscriptionExchange ex : outSocket)
				{
					ex.setSubsClass(this);
					ex.start();
				}
			}
		}

		public int getCounter()
		{
			return counter;
		}

		public void addCounter()
		{
			counter++;
		}

	}

	private static class SubscriptionExchange extends Thread
	{
		private Socket				socket;
		private Socket				outSocket;
		private SubscriptionClass	subsClass;

		public SubscriptionExchange(Socket socket, Socket outSocket)
		{
			this.socket = socket;
			this.outSocket = outSocket;
		}

		public void run()
		{
			try
			{
				while (!Hook.isTerminate())
				{
					Object o = SocketUtil.readJsonString(outSocket.getInputStream(), Resource.class, ResultSize.class,
							"resultSize");
					if (o instanceof Resource)
					{
						if (subsClass != null)
						{
							subsClass.addCounter();
						}
						Resource res = (Resource) o;
						SocketUtil.send(socket.getOutputStream(), res);
					}
					else
					{
						if (!outSocket.isClosed())
						{
							outSocket.close();
						}
					}
				}
			}
			catch (Exception e)
			{
				LoggerUtil.println("Terminated subscription relay.");
				if (!socket.isClosed())
				{
					try
					{
						socket.close();
					}
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if (!outSocket.isClosed())
				{
					try
					{
						outSocket.close();
					}
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				//e.printStackTrace();
			}
		}

		public SubscriptionClass getSubsClass()
		{
			return subsClass;
		}

		public void setSubsClass(SubscriptionClass subsClass)
		{
			this.subsClass = subsClass;
		}

		public void close()
		{
			if (outSocket != null && !outSocket.isClosed())
			{
				try
				{
					outSocket.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
