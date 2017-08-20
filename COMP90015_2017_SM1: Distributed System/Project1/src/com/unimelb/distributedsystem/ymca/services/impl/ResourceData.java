package com.unimelb.distributedsystem.ymca.services.impl;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.unimelb.distributedsystem.ymca.exceptions.EzShareException;
import com.unimelb.distributedsystem.ymca.model.Resource;
import com.unimelb.distributedsystem.ymca.model.ResourceTemplate;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;
import com.unimelb.distributedsystem.ymca.util.SecretKeyUtil;
import com.unimelb.distributedsystem.ymca.util.StringUtil;
import com.unimelb.distributedsystem.ymca.util.Util;

public class ResourceData
{
	//	private Set<Resource> resources = new HashSet<Resource>();
	private static Map<String,Resource> resources = new ConcurrentHashMap<String,Resource>();

	public synchronized List<Resource> fetch(ResourceTemplate template) throws EzShareException
	{
		LoggerUtil.printInfo("fetching from "+template.getEzServer());
		LoggerUtil.println("Starting fetch.");
		LoggerUtil.println(" To fetch: " + Util.toJson(template));
		List<Resource> retVal = new ArrayList<Resource>();
		
		Resource r = getResourcePresent(template);
		if(null!=r)
			retVal.add(r);
		LoggerUtil.println("Fetch result: " + Util.toJson(retVal));
		return retVal;
	}

	public synchronized List<Resource> query(ResourceTemplate template) throws EzShareException
	{
		LoggerUtil.printInfo("quering from "+template.getEzServer());
		LoggerUtil.println("Starting query.");
		LoggerUtil.println(" To query: " + Util.toJson(template));
		List<Resource> retVal = new ArrayList<Resource>();
		for (Entry<String, Resource> res : resources.entrySet())
		{
			if (queryCompare(res.getValue(), template))
			{
				retVal.add(res.getValue().clone());
			}
		}
		LoggerUtil.println("Query result: " + Util.toJson(retVal));
		return retVal;
	}

	public synchronized void publish(Resource resource) throws EzShareException
	{
		LoggerUtil.printInfo("publishing to "+resource.getEzServer());
		LoggerUtil.println("Publishing: " + Util.toJson(resource));
		validatePublish(resource);
		resources.put(generateResourceKey(resource),resource);
	}

	public synchronized void share(Resource resource,String secretKey) throws EzShareException
	{
		LoggerUtil.printInfo("sharing to "+resource.getEzServer());
		LoggerUtil.println("Sharing: " + Util.toJson(resource));
		validateShare(resource,secretKey);
		resources.put(generateResourceKey(resource),resource);
	}

	public synchronized boolean remove(Resource resource) throws EzShareException
	{
		LoggerUtil.printInfo("removing from "+resource.getEzServer());
		LoggerUtil.println("Removing: " + Util.toJson(resource));
		validateRemove(resource);
		return resources.remove(generateResourceKey(resource),resource);
	}

	private boolean queryCompare(Resource resource, ResourceTemplate template)
	{
		LoggerUtil.println(" comparing to: " + Util.toJson(resource));
		// (The template channel equals (case sensitive) the resource channel
		// AND
		boolean channelEqual = resource.getChannel().equals(template.getChannel());
		// If the template contains an owner that is not "", then the candidate
		// owner must equal it (case sensitive) AND
		boolean ownerTemplateEmpty = StringUtil.isEmpty(template.getOwner());
		boolean ownerEquals = ownerTemplateEmpty
				|| (!ownerTemplateEmpty && resource.getChannel().equals(template.getChannel()));
		// Any tags present in the template also are present in the candidate
		// (case insensitive) AND
		boolean tagMatch = containsTags(resource.getTags(), template.getTags());
		// If the template contains a URI then the candidate URI matches (case
		// sensitive) AND
		boolean uriMatch = template.getUri() == null
				|| (template.getUri() != null && resource.getUri().equals(template.getUri()));
		// (The candidate name contains the template name as a substring (for
		// non "" template name) OR
		boolean nameSubMatch = template.getName() != null && resource.getName().contains(template.getName());
		// The candidate description contains the template description as a
		// substring (for non "" template descriptions) OR
		boolean descSubMatch = template.getDescription() != null
				&& resource.getDescription().contains(template.getDescription());
		// The template description and name are both ""))
		boolean nameDescNull = StringUtil.isEmpty(template.getDescription()) && StringUtil.isEmpty(template.getName());
		LoggerUtil.println(" --> ", "channelEqual=" + channelEqual, "ownerEquals=" + ownerEquals,
				"tagMatch=" + tagMatch, "uriMatch=" + uriMatch, "nameSubMatch=" + nameSubMatch,
				"descSubMatch=" + descSubMatch, "nameDescNull=" + nameDescNull);
		return channelEqual && ownerEquals && tagMatch && uriMatch && (nameSubMatch || descSubMatch || nameDescNull);
	}

	private void validatePublish(Resource res) throws EzShareException
	{
		validateOwner(res.getOwner());
		if(isResourceFileScheme(res) || isURIHasDifferentOwner(res) || !isURIAbsolute(res))
			throw new EzShareException("invalid resource");
	}

	private void validateShare(Resource res,String secretKey) throws EzShareException
	{
		validateOwner(res.getOwner());
		if(!isValidSeceretKey(secretKey))
			throw new EzShareException("missing resource and/or secret");
		if(!isResourceFileScheme(res) || isURIHasDifferentOwner(res) ||!isURIAbsolute(res) || !isURILocationExist(res))
			throw new EzShareException("invalid resource");

	}

	private boolean containsTags(List<String> source, List<String> template)
	{

		Boolean bool = false;
		if(Util.checkListNotNull(template)){
			bool = true;
		}else if (Util.checkListNotNull(source)){
			List<String> list = new ArrayList<String>(source);
			list.retainAll(template);
			bool = list.size()>0;
		}

		return bool;

	}

	private static String generateResourceKey(Resource resource){
		return resource.getOwner()+"_"+resource.getChannel()+"_"+resource.getUri();
	}

	private static boolean isURILocationExist(Resource resource){
		File file = null;
		if(isResourceFileScheme(resource)){
			file = new File(resource.getUri().getSchemeSpecificPart());
		}else{
			return false;
		}
		return file.exists();
	}

	private static boolean isResourceFileScheme(Resource resource){
		String scheme = resource.getUri().getScheme();
		if(null!= scheme && !scheme.isEmpty() && scheme.equalsIgnoreCase("file")){
			return true;
		}
		return false;
	}

	private static void validateRemove(Resource res) throws EzShareException{
		validateOwner(res.getOwner());
		if(!isResourceExist(res))
			throw new EzShareException("cannot remove resource");
	}

	private static void validateOwner(String owner) throws EzShareException{
		if(null!=owner && !StringUtil.isEmpty(owner) && (owner.length() ==1 || "*".equalsIgnoreCase(owner)))
			throw new EzShareException("invalid resource");
	}

	private static boolean isValidSeceretKey(String secret) throws EzShareException{
		boolean bool = false;
		if(SecretKeyUtil.getKey().equals(secret))
			bool = true;

		return bool;
	}

	private static boolean isResourceExist(Resource res) throws EzShareException{
		if(null == resources.get(generateResourceKey(res)))
			return false;
		else 
			return true;
	}

	private static boolean isURIHasDifferentOwner(Resource res){
		boolean bool = false;
		String resourcekeyWithoutOwner = getKeyWithoutOwner(res);
		if(!resources.isEmpty()){
			Set<String> resourceKeys = resources.keySet();

			for(String key:resourceKeys){
				if(key.endsWith(resourcekeyWithoutOwner) && !key.substring(0, key.indexOf("_")).equals(res.getOwner())){
					bool = true;
					break;
				}
			}
		}
		return bool;
	}
	
	private static Resource getResourcePresent(ResourceTemplate res){
	    Resource resourcesList = null;
		String resourcekeyWithoutOwner = getKeyWithoutOwner(res);
		if(!resources.isEmpty()){
			for(Entry<String,Resource> entry:resources.entrySet()){
				if(entry.getKey().endsWith(resourcekeyWithoutOwner)){
					resourcesList = entry.getValue();
				}
			}
		}
		return resourcesList;
	}
	
	private static String getKeyWithoutOwner(Resource res){
		return res.getChannel()+"_"+res.getUri().toString();
	}
	
	private static boolean isURIAbsolute(Resource res){
		return res.getUri().isAbsolute();
	}
}
