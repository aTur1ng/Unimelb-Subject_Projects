package EZShare;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.LogManager;

import com.unimelb.distributedsystem.ymca.ClientOptions;
import com.unimelb.distributedsystem.ymca.consts.Constants;
import com.unimelb.distributedsystem.ymca.model.Resource;
import com.unimelb.distributedsystem.ymca.model.ServerQueryFetchResponse;
import com.unimelb.distributedsystem.ymca.model.ServerRequest;
import com.unimelb.distributedsystem.ymca.model.ServerResponse;
import com.unimelb.distributedsystem.ymca.util.CommandUtil;
import com.unimelb.distributedsystem.ymca.util.FileUtil;
import com.unimelb.distributedsystem.ymca.util.KeyUtil;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;
import com.unimelb.distributedsystem.ymca.util.SocketUtil;
import com.unimelb.distributedsystem.ymca.util.Util;

/**
 * Client starts here
 * 
 * @author alaguipo
 *
 */
public class Client extends Thread
{

	public static void main(String[] args)
	{
		// System.setProperty("javax.net.ssl.keyStore",
		// "src/main/resources/keystore.jks");
		// System.setProperty("javax.net.ssl.keyStorePassword", "password");
		// System.setProperty("javax.net.ssl.trustStore",
		// "src/main/resources/trustStore");
		FileUtil.extractFilesFromJar("/keystore.jks", "keystore.jks");
		FileUtil.extractFilesFromJar("/trustStore", "trustStore");
		System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "password");
		System.setProperty("javax.net.ssl.trustStore", "trustStore");

		System.setProperty("javax.net.debug", "all");
		// System.out.println("Starting client launcher");
		// Get properties from arguments
		Properties props = Util.generatePropertiesFromArguments(args);
		// Generate options convenience object
		ClientOptions options = new ClientOptions(props);
		LoggerUtil.setEnabled(LogManager.getLogger(Client.class), options.isDebug());
		options.setId(KeyUtil.getId());

		Client client = new Client(options);
		client.start();
		if (options.isSubscribe())
		{
			try
			{
				LoggerUtil.println("Press [ ENTER ] to end subscribe...");
				System.in.read();
				options.unsubscribe();
				Client clientUnsubscribe = new Client(options);
				clientUnsubscribe.start();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private ClientOptions options;

	public Client(ClientOptions options)
	{
		this.options = options;
	}

	@Override
	public void run()
	{
		// LoggerUtil.printInfo("Client is Starting");
		// LoggerUtil.println("Client props: " + props);
		// LoggerUtil.println("Client options: " + Util.toJson(options));

		boolean secure = options.isSecure();
		int portToUse = secure ? options.getSecurePort() : options.getPort();
		// Create a command request
		ServerRequest request = CommandUtil.createCommand(options);
		// Send to host and get response
		try (Socket socket = SocketUtil.createSocket(options.getHost(), portToUse, secure);)
		{
			//socket.setSoTimeout(Constants.TIMEOUT);
			LoggerUtil.println("SENT: " + Util.toPrettyJson(request));
			SocketUtil.send(socket.getOutputStream(), request);
			if (request.getComm() == ServerRequest.Command.FETCH || request.getComm() == ServerRequest.Command.QUERY
					|| request.getComm() == ServerRequest.Command.SUBSCRIBE)
			{
				ServerQueryFetchResponse response = SocketUtil.readQueryResponse(socket.getInputStream());
				if (response != null)
				{
					LoggerUtil.println("RECEIVED: " + Util.toPrettyJson(response.getResponse()));
					for (Resource resource : response.getResource())
					{
						LoggerUtil.printInfo("  Server resource: " + Util.toPrettyJson(resource));
					}
					if (response.getRawFile() != null)
					{
						LoggerUtil.printInfo(
								"  Received file content: " + DatatypeConverter.printHexBinary(response.getRawFile()));
						FileUtil.writeBytes(response.getResource().get(0), response.getRawFile());
					}
					LoggerUtil.printInfo("  Size: " + Util.toPrettyJson(response.getSize()));
				}
			}
			else
			{
				ServerResponse response = SocketUtil.readJsonString(socket.getInputStream(), ServerResponse.class);
				LoggerUtil.printInfo("Server response: " + Util.toPrettyJson(response));
			}
			// getSocketValue(request, socket);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
