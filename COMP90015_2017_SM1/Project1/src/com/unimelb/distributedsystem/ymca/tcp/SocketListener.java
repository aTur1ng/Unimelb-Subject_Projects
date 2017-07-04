package com.unimelb.distributedsystem.ymca.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.unimelb.distributedsystem.ymca.ServerOptions;
import com.unimelb.distributedsystem.ymca.consts.Constants;
import com.unimelb.distributedsystem.ymca.model.ServerAddress;
import com.unimelb.distributedsystem.ymca.services.ResourceService;
import com.unimelb.distributedsystem.ymca.services.ServerListService;
import com.unimelb.distributedsystem.ymca.services.ServiceProvider;
import com.unimelb.distributedsystem.ymca.services.SocketHandlerService;
import com.unimelb.distributedsystem.ymca.services.impl.SocketHandlerServiceImpl;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;

/**
 * Listeners
 * 
 * @author alaguipo
 *
 */
public class SocketListener extends Thread
{
	private int					port;
	private int					connectInterval;
	private int					timeout	= Constants.SERVER_TIMEOUT;
	private ServerListService	serverService;
	private ResourceService		resourceService;
	private ExecutorService		executorService;
	private ServerAddress		myServer;

	public SocketListener(ServerOptions options, ServerListService serverService, ResourceService resourceService)
	{
		this.port = options.getPort();
		this.connectInterval = options.getConnectionInterval();
		this.serverService = serverService;
		this.resourceService = resourceService;
		this.myServer = new ServerAddress(options.getAdvertiseHostName(), options.getPort());
		executorService = Executors.newFixedThreadPool(Constants.MAX_THREAD_COUNT);
	}

	private Map<String, Long> connectionMap = new HashMap<String, Long>();

	private boolean validateConnection(String hostName)
	{
		Long lastConnect = connectionMap.get(hostName);
		if (lastConnect == null || (System.currentTimeMillis() - lastConnect > connectInterval))
		{
			connectionMap.put(hostName, System.currentTimeMillis());
			return true;
		}
		return false;
	}

	@Override
	public void run()
	{
		LoggerUtil.println("Starting Socket Listener (port=" + port + ")." + Hook.isTerminate());
		ServerSocket listener;
		try
		{
			listener = new ServerSocket(port);
			listener.setSoTimeout(timeout);
			while (!Hook.isTerminate())
			{
				try
				{
					Socket socket = listener.accept();
					LoggerUtil.println("Connection received from: " + socket.getInetAddress().toString());
					if (validateConnection(socket.getInetAddress().getHostName()))
					{
						SocketHandlerService handler = ServiceProvider.getSocketHandlerService(socket, serverService,
								resourceService, myServer);
						executorService.submit(handler);
					}
					else
					{
						LoggerUtil.println("Too soon. Disconnecting " + socket.getInetAddress().toString());
						socket.close();
					}
				}
				catch (SocketTimeoutException ste)
				{
					continue;
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LoggerUtil.println("Terminating Socket Listener." + Hook.isTerminate());
		Hook.terminate();
	}
}
