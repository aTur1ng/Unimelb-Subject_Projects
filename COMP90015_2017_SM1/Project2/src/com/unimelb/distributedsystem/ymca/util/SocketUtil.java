package com.unimelb.distributedsystem.ymca.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.DatatypeConverter;

import com.unimelb.distributedsystem.ymca.model.Resource;
import com.unimelb.distributedsystem.ymca.model.ResultSize;
import com.unimelb.distributedsystem.ymca.model.ServerQueryFetchResponse;
import com.unimelb.distributedsystem.ymca.model.ServerResponse;

public class SocketUtil
{

	public static Socket createSocket(String host, int port, boolean secure) throws UnknownHostException, IOException
	{
		if (!secure)
		{
			return SocketFactory.getDefault().createSocket(host, port);
		}
		else
		{
			return SSLSocketFactory.getDefault().createSocket(host, port);
		}
	}

	public static ServerSocket createServerSocket(int port, boolean secure) throws IOException
	{
		if (!secure)
		{
			ServerSocketFactory ssf = ServerSocketFactory.getDefault();
			return ssf.createServerSocket(port);
		}
		else
		{
			SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			return ssf.createServerSocket(port);
		}
	}

	/**
	 * Send ServerResponse object to ou\\
	 * 
	 * @param out
	 * @param response
	 * @throws IOException
	 */
	public static void send(OutputStream out, Object response) throws IOException
	{
		DataOutputStream ou = new DataOutputStream(out);
		String json = Util.toJson(response);
		LoggerUtil.println("Sending " + response.getClass().getSimpleName() + " to output stream: " + json);
		ou.writeUTF(json);
		ou.flush();
	}

	/**
	 * Send ServerRequest object to ou\\
	 * 
	 * @param out
	 * @param response
	 * @throws IOException
	 */
	public static void send(OutputStream out, byte[] rawBytes) throws IOException
	{
		out.write(rawBytes);
		out.flush();
	}

	/**
	 * Read a string from stream
	 * 
	 * @param out
	 * @param cls
	 * @return
	 * @throws IOException
	 */
	public static String readString(InputStream ints) throws IOException
	{
		DataInputStream in = new DataInputStream(ints);
		String read = in.readUTF();
		LoggerUtil.println("Reading string from input stream: " + read);
		return read;
	}

	/**
	 * Read a json from stream
	 * 
	 * @param out
	 * @param cls
	 * @return
	 * @throws IOException
	 */
	public static <T> T readJsonString(InputStream ints, Class<T> cls) throws IOException
	{
		DataInputStream in = new DataInputStream(ints);
		String jsonString = in.readUTF();
		LoggerUtil.println("RECEIVED: " + jsonString);
		return Util.fromJson(jsonString, cls);
	}

	/**
	 * Read a json from stream
	 * 
	 * @param out
	 * @param cls
	 * @return
	 * @throws IOException
	 */
	public static <T, U> Object readJsonString(InputStream ints, Class<T> cls1, Class<U> cls2, String markerForClass2)
			throws IOException
	{
		DataInputStream in = new DataInputStream(ints);
		String jsonString = in.readUTF();
		LoggerUtil.println("RECEIVED: " + jsonString);
		if (jsonString.contains(markerForClass2))
		{
			return Util.fromJson(jsonString, cls2);
		}
		else
		{
			return Util.fromJson(jsonString, cls1);
		}
	}

	public static byte[] readRawBytes(InputStream ints, int byteLen) throws IOException
	{
		byte[] retVal = new byte[byteLen];
		for (int i = 0; i < retVal.length; i++)
		{
			retVal[i] = (byte) ints.read();
		}
		LoggerUtil.println("Reading bytes from input stream: " + DatatypeConverter.printHexBinary(retVal));
		return retVal;
	}

	public static ServerQueryFetchResponse readQueryResponse(InputStream ints) throws IOException
	{
		ServerQueryFetchResponse retVal = new ServerQueryFetchResponse();

		ServerResponse response = SocketUtil.readJsonString(ints, ServerResponse.class);
		LoggerUtil.println("Received response: " + Util.toJson(response));
		retVal.setResponse(response);
		if (response.getResponse().equalsIgnoreCase("success"))
		{
			Object o = SocketUtil.readJsonString(ints, Resource.class, ResultSize.class, "resultSize");
			if (o instanceof Resource)
			{
				Resource resource = (Resource) o;
				// fetch
				if (resource.getResourceSize() != null && resource.getResourceSize() > 0)
				{
					retVal.addResource(resource);
					LoggerUtil.println("Received from socket: " + Util.toJson(resource));
					byte[] file = SocketUtil.readRawBytes(ints, resource.getResourceSize());
					retVal.setRawFile(file);
					LoggerUtil.println("Received file content: " + DatatypeConverter.printHexBinary(file));
					ResultSize size = SocketUtil.readJsonString(ints, ResultSize.class);
					retVal.setSize(size);
					LoggerUtil.println("Received result size: " + size);
				}
				else
				{
					while (o != null && o instanceof Resource)
					{
						retVal.addResource((Resource) o);
						LoggerUtil.println(
								"Received from socket(" + o.getClass().getSimpleName() + "): " + Util.toJson(o));
						try
						{
							o = SocketUtil.readJsonString(ints, Resource.class, ResultSize.class, "resultSize");
						}
						catch (Exception e)
						{
							o = null;
						}
					}
					retVal.setSize((ResultSize) o);
				}
			}
			else if (o instanceof ResultSize)
			{
				retVal.setSize((ResultSize) o);
			}
		}
		return retVal;
	}

}
