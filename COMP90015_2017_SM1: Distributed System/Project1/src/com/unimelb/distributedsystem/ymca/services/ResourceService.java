package com.unimelb.distributedsystem.ymca.services;

import java.io.IOException;
import java.util.List;

import com.unimelb.distributedsystem.ymca.exceptions.EzShareException;
import com.unimelb.distributedsystem.ymca.model.Resource;
import com.unimelb.distributedsystem.ymca.model.ServerRequest;

//TODO: Redefine interface/Implement by Yogesh
public interface ResourceService
{
	public List<Resource> execute(String command, String secret, ServerRequest resource) throws EzShareException;

}