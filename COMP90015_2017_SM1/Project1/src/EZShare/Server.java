package EZShare;


import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.unimelb.distributedsystem.ymca.ServerOptions;
import com.unimelb.distributedsystem.ymca.services.ResourceService;
import com.unimelb.distributedsystem.ymca.services.ServerListService;
import com.unimelb.distributedsystem.ymca.services.ServiceProvider;
import com.unimelb.distributedsystem.ymca.tcp.Exchanger;
import com.unimelb.distributedsystem.ymca.tcp.SocketListener;
import com.unimelb.distributedsystem.ymca.util.LoggerUtil;
import com.unimelb.distributedsystem.ymca.util.Util;

/**
 * Server starts here
 * @author alaguipo
 *
 */
public class Server
{
	public static void main(String[] args)
	{
		//System.out.println("Starting server launcher");
		// Generate properties from args
		Properties properties = Util.generatePropertiesFromArguments(args);
		// Create options convenience class
		ServerOptions options = new ServerOptions(properties);
		LoggerUtil.setEnabled(LogManager.getLogger(Server.class),options.isDebug());
		LoggerUtil.println("Server props: " + properties);
		LoggerUtil.println("Server options: " + Util.toJson(options));
		
		LoggerUtil.printInfo("Starting the EZShare Server");
		LoggerUtil.printInfo("using secret: "+options.getSecret());
		LoggerUtil.printInfo("using advertised hostname: "+options.getAdvertiseHostName());
		LoggerUtil.printInfo("bound to port: "+options.getPort());
		
		ResourceService resourceService = ServiceProvider.getResourceService();
		ServerListService serverService = ServiceProvider.getServerService();
		// Create a socket connections listener
		SocketListener listener = new SocketListener(options, serverService, resourceService);
		// Create an exchanger
		Exchanger exchanger = new Exchanger(options, serverService);

		listener.start();
		exchanger.start();
		LoggerUtil.printInfo("started");
		try
		{
			listener.join();
			exchanger.join();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
