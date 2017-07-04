package com.unimelb.distributedsystem.ymca.services.dummy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unimelb.distributedsystem.ymca.exceptions.EzShareException;
import com.unimelb.distributedsystem.ymca.model.Resource;
import com.unimelb.distributedsystem.ymca.model.ResourceTemplate;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;
import com.unimelb.distributedsystem.ymca.util.StringUtil;
import com.unimelb.distributedsystem.ymca.util.Util;

public class ResourceData
{
	private Set<Resource> resources = new HashSet<Resource>();

	public synchronized List<Resource> fetch(ResourceTemplate template) throws EzShareException
	{
		LoggerUtil.println("Starting fetch.");
		LoggerUtil.println(" To fetch: " + Util.toJson(template));
		List<Resource> retVal = new ArrayList<Resource>();
		for (Resource res : resources)
		{
			LoggerUtil.println(" comparing to: " + Util.toJson(res));
			if (res.getChannel().equals(template.getChannel()) && res.getUri().equals(template.getUri()))
			{
				retVal.add(res);
				break;
			}
		}
		LoggerUtil.println("Fetch result: " + Util.toJson(retVal));
		return retVal;
	}

	public synchronized List<Resource> query(ResourceTemplate template) throws EzShareException
	{
		LoggerUtil.println("Starting query.");
		LoggerUtil.println(" To query: " + Util.toJson(template));
		List<Resource> retVal = new ArrayList<Resource>();
		for (Resource res : resources)
		{
			if (queryCompare(res, template))
			{
				retVal.add(res.clone());
			}
		}
		LoggerUtil.println("Query result: " + Util.toJson(retVal));
		return retVal;
	}

	public synchronized void publish(Resource resource) throws EzShareException
	{
		LoggerUtil.println("Publishing: " + Util.toJson(resource));
		validatePublish(resource);
		resources.add(resource);
	}

	public synchronized void share(Resource resource, String secret) throws EzShareException
	{
		LoggerUtil.println("Sharing: " + Util.toJson(resource));
		validateShare(resource);
		resources.add(resource);
	}

	public synchronized boolean remove(Resource resource) throws EzShareException
	{
		LoggerUtil.println("Removing: " + Util.toJson(resource));
		return resources.remove(resource);
	}

	public static boolean queryCompare(Resource resource, ResourceTemplate template)
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
		for (Resource r : resources)
		{
			if (!res.getOwner().equalsIgnoreCase(r.getOwner()) && res.getChannel().equalsIgnoreCase(r.getChannel())
					&& res.getUri().compareTo(r.getUri()) == 0)
			{
				throw new EzShareException("cannot publish resource");
			}
		}
	}

	private void validateShare(Resource res) throws EzShareException
	{
		for (Resource r : resources)
		{
			if (!res.getOwner().equalsIgnoreCase(r.getOwner()) && res.getChannel().equalsIgnoreCase(r.getChannel())
					&& res.getUri().compareTo(r.getUri()) == 0)
			{
				throw new EzShareException("cannot share resource");
			}
		}
	}

	public static boolean containsTags(List<String> source, List<String> template)
	{
		if (template == null || template.size() <= 0)
		{
			return true;
		}
		if (source == null || source.size() <= 0)
		{
			return false;
		}
		for (String tmpTag : template)
		{
			if (source.contains(tmpTag))
			{
				return true;
			}
		}
		return false;
	}
}
