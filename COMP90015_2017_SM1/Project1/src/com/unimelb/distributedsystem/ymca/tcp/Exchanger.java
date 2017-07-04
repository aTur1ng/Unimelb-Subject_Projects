package com.unimelb.distributedsystem.ymca.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.unimelb.distributedsystem.ymca.ServerOptions;
import com.unimelb.distributedsystem.ymca.consts.Constants;
import com.unimelb.distributedsystem.ymca.model.ServerAddress;
import com.unimelb.distributedsystem.ymca.model.ServerRequest;
import com.unimelb.distributedsystem.ymca.model.ServerResponse;
import com.unimelb.distributedsystem.ymca.services.ServerListService;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;
import com.unimelb.distributedsystem.ymca.util.SocketUtil;
import com.unimelb.distributedsystem.ymca.util.Util;

public class Exchanger extends Thread
{
	private int					interval;
	private ServerListService	service;
	private ServerAddress myServer;

	public Exchanger(ServerOptions options, ServerListService service)
	{
		this.interval = options.getExchangeInterval();
		this.service = service;
		this.myServer = new ServerAddress(options.getAdvertiseHostName(), options.getPort());
	}

	@Override
	public void run()
	{
		LoggerUtil.println("Starting Exchanger." + Hook.isTerminate());
		while (!Hook.isTerminate())
		{
			try
			{
				LoggerUtil.println("Sleeping for " + interval + " ms.");
				Thread.sleep(interval);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Send server list to a random server
			sendServerListToRandomServer();
		}
		LoggerUtil.println("Terminating Exchanger." + Hook.isTerminate());
	}

	private void sendServerListToRandomServer()
	{
		LoggerUtil.printInfo("Starting Exchange Send.");
		List<ServerAddress> serverList = service.getServerList(myServer);
		ServerAddress address = service.getRandomServer(myServer);
		LoggerUtil.println("Send to: " + address);
		LoggerUtil.println("Server list: " + serverList);
		// If count = 1, we'll be giving the server itself
		if (address != null && serverList.size() >= 1)
		{
			try (Socket socket = new Socket(address.getHostname(), address.getPort());)
			{
				socket.setSoTimeout(Constants.TIMEOUT);
				ServerRequest request = new ServerRequest();
				request.setCommand(ServerRequest.Command.EXCHANGE);
				request.setServerList(serverList);
				SocketUtil.send(socket.getOutputStream(), request);
				ServerResponse response = SocketUtil.readJsonString(socket.getInputStream(), ServerResponse.class);
				LoggerUtil.printInfo("Response recieved");
				LoggerUtil.println("Server response: " + Util.toJson(response));
			}
			catch (IOException e)
			{
				e.printStackTrace();
				service.removeServer(address);
			}
		}
		else
		{
			LoggerUtil.printInfo("To few entries in the serverlist to send");
		}
	}
}