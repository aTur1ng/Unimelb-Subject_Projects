package com.unimelb.distributedsystem.ymca.util;

import java.io.IOException;
import java.io.InputStream;

import javax.lang.model.SourceVersion;

import com.unimelb.distributedsystem.ymca.ClientOptions;
import com.unimelb.distributedsystem.ymca.model.Resource;
import com.unimelb.distributedsystem.ymca.model.ResourceTemplate;
import com.unimelb.distributedsystem.ymca.model.ServerRequest;

public class CommandUtil
{
	/**
	 * Convert an incoming input stream (from a client) to a ServerRequest
	 * object
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static ServerRequest convertCommand(InputStream in) throws IOException
	{
		ServerRequest res = SocketUtil.readJsonString(in, ServerRequest.class);
		return res;
	}

	/**
	 * Create a ServerRequest object from properties
	 * 
	 * @param props
	 * @return
	 */
	public static ServerRequest createCommand(ClientOptions options)
	{
		ServerRequest request = new ServerRequest();
		ServerRequest.Command command = getCommand(options);
		request.setCommand(command);
		request.setRelay(options.isRelay());
		if (command == ServerRequest.Command.FETCH || command == ServerRequest.Command.SHARE)
		{
			request.setSecret(options.getSecret());
		}
		switch (command)
		{
		case EXCHANGE:
			request.setServerList(options.getServers());
			break;
		case FETCH:
		case QUERY:
			ResourceTemplate resourceTemplate = new ResourceTemplate(options.getChannel(), options.getOwner(),
					options.getUri());
			resourceTemplate.setDescription(options.getDescription());
			resourceTemplate.setName(options.getName());
			resourceTemplate.setEzServer(options.getHost() + ":" + options.getPort());
			resourceTemplate.setTags(options.getTags());
			resourceTemplate.setUri(options.getUri());
			request.setResourceTemplate(resourceTemplate);
			break;
		case SUBSCRIBE:
			ResourceTemplate resourceTemplate2 = new ResourceTemplate(options.getChannel(), options.getOwner(),
					options.getUri());
			resourceTemplate2.setDescription(options.getDescription());
			resourceTemplate2.setName(options.getName());
			resourceTemplate2.setEzServer(options.getHost() + ":" + options.getPort());
			resourceTemplate2.setTags(options.getTags());
			resourceTemplate2.setUri(options.getUri());
			request.setId(options.getId());
			request.setResourceTemplate(resourceTemplate2);
			break;
		case UNSUBSCRIBE:
			request.setId(options.getId());
			break;
		case PUBLISH:
		case REMOVE:
		case SHARE:
			Resource resource = new Resource(options.getChannel(), options.getOwner(), options.getUri());
			resource.setDescription(options.getDescription());
			resource.setName(options.getName());
			resource.setEzServer(options.getHost() + ":" + options.getPort());
			resource.setTags(options.getTags());
			resource.setUri(options.getUri());
			request.setResource(resource);
			break;
		default:
			break;
		}
		return request;
	}

	private static ServerRequest.Command getCommand(ClientOptions options)
	{
		if (options.isExchange())
		{
			LoggerUtil.printInfo("exchanging from " + options.getHost() + ":" + options.getPort());
			return ServerRequest.Command.EXCHANGE;
		}
		if (options.isFetch())
		{
			LoggerUtil.printInfo("fetching from " + options.getHost() + ":" + options.getPort());
			return ServerRequest.Command.FETCH;
		}
		if (options.isPublish())
		{
			LoggerUtil.printInfo("publishing to " + options.getHost() + ":" + options.getPort());
			return ServerRequest.Command.PUBLISH;
		}
		if (options.isQuery())
		{
			LoggerUtil.printInfo("quering from " + options.getHost() + ":" + options.getPort());
			return ServerRequest.Command.QUERY;
		}
		if (options.isRemove())
		{
			LoggerUtil.printInfo("removing from " + options.getHost() + ":" + options.getPort());
			;
			return ServerRequest.Command.REMOVE;
		}
		if (options.isShare())
		{
			LoggerUtil.printInfo("shareing to " + options.getHost() + ":" + options.getPort());
			return ServerRequest.Command.SHARE;
		}
		if (options.isSubscribe())
		{
			LoggerUtil.printInfo("subscribe from " + options.getHost() + ":" + options.getPort());
			return ServerRequest.Command.SUBSCRIBE;
		}
		if (options.isUnsubscribe())
		{
			LoggerUtil.printInfo("unsubscribe from " + options.getHost() + ":" + options.getPort());
			return ServerRequest.Command.UNSUBSCRIBE;
		}
		return null;
	}
}
