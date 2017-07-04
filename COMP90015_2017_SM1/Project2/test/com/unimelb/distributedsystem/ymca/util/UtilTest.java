package com.unimelb.distributedsystem.ymca.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.unimelb.distributedsystem.ymca.model.Resource;

public class UtilTest
{

	@Test
	public void testGeneratePropertiesFromArguments()
	{
		String[] tst = new String[]
		{ "-exchange", "-servers", "115.146.85.165:3780,115.146.85.24:3780", "-debug" };
		Properties props = Util.generatePropertiesFromArguments(tst);
		System.out.println(props);
		assertNotNull(props);
		assertEquals("true", props.getProperty("debug"));
		assertEquals("true", props.getProperty("exchange"));
		assertEquals("115.146.85.165:3780,115.146.85.24:3780", props.getProperty("servers"));
		assertNull(props.getProperty("client"));
	}

	@Test
	public void testJson()
	{
		Resource resource = new Resource("channel", "owner", URI.create("http://www.google.com"));
		resource.setDescription("desription");
		resource.setName("name");
		List<String> tags = Arrays.asList("tag1", "tag2");
		resource.setTags(tags);

		String jsonString = Util.toJson(resource);
		assertNotNull(jsonString);
		System.out.println(jsonString);

		Resource retRes = Util.fromJson(jsonString, Resource.class);
		assertNotNull(retRes);
		assertEquals(resource, retRes);
		assertEquals(resource.getDescription(), retRes.getDescription());
		assertEquals(resource.getName(), retRes.getName());
		assertEquals(resource.getTags().size(), retRes.getTags().size());
	}

}
