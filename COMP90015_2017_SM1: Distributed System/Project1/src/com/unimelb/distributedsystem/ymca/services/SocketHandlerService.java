package com.unimelb.distributedsystem.ymca.services;

import java.net.Socket;

import com.unimelb.distributedsystem.ymca.model.ServerAddress;

public abstract class SocketHandlerService implements Runnable
{
	Socket				socket;
	ServerListService	serverService;
	ResourceService		resourceService;
	ServerAddress		myServer;

	public SocketHandlerService(Socket socket, ServerListService serverService, ResourceService resourceService,
			ServerAddress myServer)
	{
		this.socket = socket;
		this.serverService = serverService;
		this.resourceService = resourceService;
		this.myServer = myServer;
	}

	@Override
	public void run()
	{
		handleSocket(socket, serverService, resourceService, myServer);
	}

	public abstract void handleSocket(Socket socket, ServerListService serverService, ResourceService resourceService,
			ServerAddress myServer);
}
