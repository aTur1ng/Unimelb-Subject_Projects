package com.unimelb.distributedsystem.ymca.services;

import java.net.Socket;

import com.unimelb.distributedsystem.ymca.model.ServerAddress;
import com.unimelb.distributedsystem.ymca.services.dummy.ResourceServiceDummy;
import com.unimelb.distributedsystem.ymca.services.dummy.ServerListServiceDummy;
import com.unimelb.distributedsystem.ymca.services.dummy.SocketHandlerServiceDummy;
import com.unimelb.distributedsystem.ymca.services.impl.ResourceServiceImpl;
import com.unimelb.distributedsystem.ymca.services.impl.SocketHandlerServiceImpl;

//TODO: return created services here
/**
 * Returns services
 * 
 * @author alaguipo
 *
 */
public class ServiceProvider
{
	public static ResourceService getResourceService()
	{
		// return new ResourceServiceImpl();
		return new ResourceServiceImpl();
	}

	public static ServerListService getServerService()
	{
		// return new ServerListServiceImpl();
		return new ServerListServiceDummy();
	}

	public static SocketHandlerService getSocketHandlerService(Socket socket, ServerListService serverService,
			ResourceService resourceService, ServerAddress myServer)
	{
		// return new SocketHandlerServiceImpl(socket, serverService,
		// resourceService);
		return new SocketHandlerServiceDummy(socket, serverService, resourceService, myServer);
	}

}
