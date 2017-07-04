package com.unimelb.distributedsystem.ymca.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerQueryFetchResponse
{
	private ServerResponse	response;
	private List<Resource>	resource	= new ArrayList<Resource>();
	private byte[]			rawFile;
	private ResultSize		size;

	public ServerResponse getResponse()
	{
		return response;
	}

	public void setResponse(ServerResponse response)
	{
		this.response = response;
	}

	public List<Resource> getResource()
	{
		return resource;
	}

	public void addResource(Resource... resource)
	{
		this.resource.addAll(Arrays.asList(resource));
	}

	public void addResource(List<Resource> resource)
	{
		this.resource.addAll(resource);
	}

	public void setResource(List<Resource> resource)
	{
		this.resource = resource;
	}

	public byte[] getRawFile()
	{
		return rawFile;
	}

	public void setRawFile(byte[] rawFile)
	{
		this.rawFile = rawFile;
	}

	public ResultSize getSize()
	{
		return size;
	}

	public void setSize(ResultSize size)
	{
		this.size = size;
	}
}
