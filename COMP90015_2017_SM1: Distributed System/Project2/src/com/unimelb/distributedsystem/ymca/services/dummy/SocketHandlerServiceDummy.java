package com.unimelb.distributedsystem.ymca.services.dummy;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
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
import com.unimelb.distributedsystem.ymca.util.KeyUtil;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;
import com.unimelb.distributedsystem.ymca.util.SocketUtil;
import com.unimelb.distributedsystem.ymca.util.Util;

/**
 * 
 * @author alaguipo
 *
 */
public class SocketHandlerServiceDummy extends SocketHandlerService
{
	private static SubscriptionData subsData = new SubscriptionData();

	public SocketHandlerServiceDummy(Socket socket, ServerListService serverService, ResourceService resourceService,
			ServerAddress myServer, boolean secure)
	{
		super(socket, serverService, resourceService, myServer, secure);
	}

	public void handleSocket(Socket socket, ServerListService serverService, ResourceService resourceService,
			ServerAddress myServer, boolean secure)
	{
		LoggerUtil.println("Starting ServerSocketHandler.");
		boolean closeSocketAfter = true;
		try
		{
			ServerRequest request = SocketUtil.readJsonString(socket.getInputStream(), ServerRequest.class);
			ServerResponse response = new ServerResponse();
			switch (request.getComm())
			{
			case EXCHANGE:
				List<ServerAddress> addresses = request.getServerList();
				serverService.addServers(addresses);
				SocketUtil.send(socket.getOutputStream(), response);
				break;
			case FETCH:
				List<Resource> resFetch = resourceService.execute(request.getCommand(), request.getSecret(), request);
				if (resFetch != null && resFetch.size() > 0)
				{
					Resource thatResource = resFetch.get(0);
					byte[] bytes = FileUtil.readBytes(thatResource);
					ResultSize size = new ResultSize(1);
					thatResource.setResourceSize(bytes.length);
					SocketUtil.send(socket.getOutputStream(), response);
					SocketUtil.send(socket.getOutputStream(), thatResource);
					SocketUtil.send(socket.getOutputStream(), bytes);
					SocketUtil.send(socket.getOutputStream(), size);
				}
				else
				{
					ResultSize size = new ResultSize(0);
					SocketUtil.send(socket.getOutputStream(), response);
					SocketUtil.send(socket.getOutputStream(), size);
				}

				break;
			case QUERY:
				List<Resource> resQuery = resourceService.execute(request.getCommand(), request.getSecret(), request);
				Set<Resource> other = getFromOtherServers(myServer, request, serverService, secure);
				resQuery.addAll(other);
				if (resQuery != null && resQuery.size() > 0)
				{
					ResultSize size = new ResultSize(resQuery.size());
					SocketUtil.send(socket.getOutputStream(), response);
					for (Resource res : resQuery)
					{
						SocketUtil.send(socket.getOutputStream(), res);
					}
					SocketUtil.send(socket.getOutputStream(), size);
				}
				else
				{
					SocketUtil.send(socket.getOutputStream(), response);
					SocketUtil.send(socket.getOutputStream(), new ResultSize(0));
				}
				break;
			case PUBLISH:
				List<Resource> resPublish = resourceService.execute(request.getCommand(), request.getSecret(), request);
				subsData.compareAndSend(request.getResource());
				SocketUtil.send(socket.getOutputStream(), response);
				break;
			case SHARE:
				List<Resource> resShare = resourceService.execute(request.getCommand(), request.getSecret(), request);
				subsData.compareAndSend(request.getResource());
				SocketUtil.send(socket.getOutputStream(), response);
				break;
			case REMOVE:
				List<Resource> resRemove = resourceService.execute(request.getCommand(), request.getSecret(), request);
				SocketUtil.send(socket.getOutputStream(), response);
				break;
			case SUBSCRIBE:
				String id = request.getId();
				response.setId(id);
				List<Socket> outSockets = subscribeFromOtherServers(myServer, request, serverService, secure);
				closeSocketAfter = false;
				subsData.subscribe(id, socket, request.getResourceTemplate(), outSockets);
				SocketUtil.send(socket.getOutputStream(), response);
				break;
			case UNSUBSCRIBE:
				response.setId(request.getId());
				subsData.unsubscribe(request.getId());
				SocketUtil.send(socket.getOutputStream(), response);
				break;
			default:
				throw new InvalidParameterException("invalid command");

			}
		}
		catch (EzShareException ezse)
		{
			ServerResponse response = new ServerResponse(ezse.getMessage());
			try
			{
				SocketUtil.send(socket.getOutputStream(), response);
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			ServerResponse response = new ServerResponse("invalid resource");
			try
			{
				SocketUtil.send(socket.getOutputStream(), response);
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		finally
		{
			if (closeSocketAfter)
			{
				LoggerUtil.println("Terminating ServerSocketHandler.");
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
		}

	}

	private Set<Resource> getFromOtherServers(ServerAddress myServer, ServerRequest request,
			ServerListService serverService, boolean secure)
	{
		Set<Resource> result = new HashSet<Resource>();
		if (request.isRelay())
		{
			List<ServerAddress> addresses = serverService.getServerList(myServer);
			if (addresses != null && addresses.size() > 0)
			{
				ResourceTemplate resource = request.getResourceTemplate();
				resource.setOwner("");
				resource.setChannel("");
				request.setRelay(false);
				for (ServerAddress address : addresses)
				{
					LoggerUtil.println("Query relay to " + address);
					try (Socket socket = SocketUtil.createSocket(address.getHostname(), address.getPort(), secure);)
					{
						socket.setSoTimeout(Constants.TIMEOUT);
						SocketUtil.send(socket.getOutputStream(), request);
						ServerQueryFetchResponse response = SocketUtil.readQueryResponse(socket.getInputStream());
						if (response != null && response.getResource().size() > 0)
						{
							LoggerUtil.println("Query relay response: " + Util.toJson(response.getResponse()));
							for (Resource res : response.getResource())
							{
								res.setEzServer(address.toString());
								LoggerUtil.println("  Server resource: " + Util.toJson(res));
							}
							LoggerUtil.println("  Size: " + Util.toJson(response.getSize()));
						}

						LoggerUtil.println("Server response: " + Util.toJson(response));
					}
					catch (Throwable e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	private List<Socket> subscribeFromOtherServers(ServerAddress myServer, ServerRequest request,
			ServerListService serverService, boolean secure) throws UnknownHostException, IOException
	{
		List<Socket> result = new ArrayList<Socket>();
		if (request.isRelay())
		{
			List<ServerAddress> addresses = serverService.getServerList(myServer);
			if (addresses != null && addresses.size() > 0)
			{
				ResourceTemplate resource = request.getResourceTemplate();
				resource.setOwner("");
				resource.setChannel("");
				request.setRelay(false);
				String id = request.getId();
				for (ServerAddress address : addresses)
				{
					LoggerUtil.println("Subscribing relay to " + address);
					Socket socket = SocketUtil.createSocket(address.getHostname(), address.getPort(), secure);
					request.setId(KeyUtil.getId());
					socket.setSoTimeout(Constants.TIMEOUT);
					SocketUtil.send(socket.getOutputStream(), request);
					ServerResponse response = SocketUtil.readJsonString(socket.getInputStream(), ServerResponse.class);
					LoggerUtil.println("Received responds to relay: " + Util.toJson(response));
					result.add(socket);
				}
				request.setId(id);
			}
		}
		return result;
	}

}
