package com.unimelb.distributedsystem.ymca.services.dummy;

import java.util.List;

import com.unimelb.distributedsystem.ymca.exceptions.EzShareException;
import com.unimelb.distributedsystem.ymca.model.Resource;
import com.unimelb.distributedsystem.ymca.model.ResourceTemplate;
import com.unimelb.distributedsystem.ymca.model.ServerRequest;
import com.unimelb.distributedsystem.ymca.services.ResourceService;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;
import com.unimelb.distributedsystem.ymca.util.SecretKeyUtil;
import com.unimelb.distributedsystem.ymca.util.StringUtil;
import com.unimelb.distributedsystem.ymca.util.Util;
import com.unimelb.distributedsystem.ymca.services.impl.ResourceData;

/**
 * 
 * @author alaguipo
 *
 */
public class ResourceServiceDummy implements ResourceService
{
	ResourceData data = new ResourceData();

	@Override
	public List<Resource> execute(String command, String secret, ServerRequest request) throws EzShareException
	{
		LoggerUtil.println("execute.command=" + command);
		LoggerUtil.println("execute.secret=" + secret);
		LoggerUtil.println("execute.request=" + Util.toJson(request));
		ServerRequest.Command comms = ServerRequest.Command.valueOf(ServerRequest.Command.class, command);
		switch (comms)
		{
		case FETCH:
			assertNotNull(request.getResourceTemplate());
			assertSecret(secret);
			assertUriIsFile(request.getResourceTemplate());
			return data.fetch(request.getResourceTemplate());
		case QUERY:
			assertNotNull(request.getResourceTemplate());
			return data.query(request.getResourceTemplate());
		case PUBLISH:
			assertNotNull(request.getResource());
			assertUriIsUrl(request.getResource());
			assertOwner(request.getResource());
			data.publish(request.getResource());
			break;
		case REMOVE:
			assertNotNull(request.getResource());
			assertUri(request.getResource());
			data.remove(request.getResource());
			break;
		case SHARE:
			assertNotNull(request.getResource());
			assertSecret(secret);
			assertUriIsFile(request.getResource());
			assertOwner(request.getResource());
			data.share(request.getResource(),secret);
			break;
		default:
			throw new EzShareException("invalid command");

		}
		return null;
	}

	private void assertNotNull(Resource resource) throws EzShareException
	{
		if (resource == null)
		{
			throw new EzShareException("missing resource");
		}
	}

	private void assertNotNull(ResourceTemplate resource) throws EzShareException
	{
		if (resource == null)
		{
			throw new EzShareException("missing resourceTemplate");
		}
	}

	private void assertSecret(String secretKey) throws EzShareException
	{
		if (!SecretKeyUtil.getKey().equals(secretKey))
		{
			throw new EzShareException("incorrect secret");
		}
	}

	private void assertUri(Resource resource) throws EzShareException
	{
		if (resource == null || resource.getUri() == null)
		{
			throw new EzShareException("missing resource");
		}
	}

	private void assertUriIsUrl(Resource resource) throws EzShareException
	{
		assertUri(resource);
		if (!StringUtil.isUrl(resource.getUri()))
		{
			throw new EzShareException("invalid resource");
		}
	}

	private void assertUriIsFile(Resource resource) throws EzShareException
	{
		assertUri(resource);
		if (!StringUtil.isFileAndExists(resource.getUri()))
		{
			throw new EzShareException("invalid resource");
		}
	}

	private void assertOwner(Resource resource) throws EzShareException
	{
		if (resource == null || resource.getOwner() == null || resource.getOwner().equals("*"))
		{
			throw new EzShareException("invalid resource");
		}
	}

}
