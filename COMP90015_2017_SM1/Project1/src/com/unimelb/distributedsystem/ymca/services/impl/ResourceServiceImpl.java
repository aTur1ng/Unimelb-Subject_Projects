package com.unimelb.distributedsystem.ymca.services.impl;

import java.util.List;

import com.unimelb.distributedsystem.ymca.exceptions.EzShareException;
import com.unimelb.distributedsystem.ymca.model.Resource;
import com.unimelb.distributedsystem.ymca.model.ServerRequest;
import com.unimelb.distributedsystem.ymca.services.ResourceService;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;
import com.unimelb.distributedsystem.ymca.util.Util;

// TODO: Yogesh
public class ResourceServiceImpl implements ResourceService
{
	ResourceData data = new ResourceData();

	@Override
	public List<Resource> execute(String command, String secret, ServerRequest request) throws EzShareException
	{
		LoggerUtil.println("execute.command=" + command);
		LoggerUtil.println("execute.secret=" + secret);
		LoggerUtil.println("execute.request=" + Util.toJson(request));
		List<Resource> resourceList = null;
		ServerRequest.Command comms = ServerRequest.Command.valueOf(ServerRequest.Command.class, command);
		switch (comms)
		{
		case FETCH:
			resourceList = data.fetch(request.getResourceTemplate());
			break;
		case QUERY:
			resourceList = data.query(request.getResourceTemplate());
			break;
		case PUBLISH:
			data.publish(request.getResource());
			break;
		case REMOVE:
			data.remove(request.getResource());
			break;
		case SHARE:
			data.share(request.getResource(),secret);
			break;
		default:
			throw new EzShareException("invalid command");

		}
		return resourceList;
	}

}
