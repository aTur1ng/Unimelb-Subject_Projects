package com.unimelb.distributedsystem.ymca.services.impl;

import java.io.IOException;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unimelb.distributedsystem.ymca.consts.Constants;
import com.unimelb.distributedsystem.ymca.exceptions.EzShareException;
import com.unimelb.distributedsystem.ymca.model.Resource;
import com.unimelb.distributedsystem.ymca.model.ResourceTemplate;
import com.unimelb.distributedsystem.ymca.model.ResultSize;
import com.unimelb.distributedsystem.ymca.model.ServerAddress;
import com.unimelb.distributedsystem.ymca.model.ServerQueryFetchResponse;
import com.unimelb.distributedsystem.ymca.model.ServerRequest;
import com.unimelb.distributedsystem.ymca.model.ServerResponse;
import com.unimelb.distributedsystem.ymca.services.ResourceService;
import com.unimelb.distributedsystem.ymca.services.ServerListService;
import com.unimelb.distributedsystem.ymca.services.SocketHandlerService;
import com.unimelb.distributedsystem.ymca.util.FileUtil;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;
import com.unimelb.distributedsystem.ymca.util.SocketUtil;
import com.unimelb.distributedsystem.ymca.util.Util;

//TODO Implement by Michael
/**
 * Handle the socket messages on the server side. This also handles and
 * validates the commands receive from the client.
 * 
 * @author michael
 *
 */
public class SocketHandlerServiceImpl extends SocketHandlerService
{

	public SocketHandlerServiceImpl(Socket socket, ServerListService serverService, ResourceService resourceService,
			ServerAddress myServer)
	{
		super(socket, serverService, resourceService, myServer);
	}

	@Override
	public void handleSocket(Socket socket, ServerListService serverService, ResourceService resourceService,
			ServerAddress myServer)
	{

	}

}
