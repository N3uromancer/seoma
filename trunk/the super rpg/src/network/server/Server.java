package network.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;

import network.Connection;
import network.ConnectionManager;
import network.IOConstants;
import network.operationExecutor.OperationExecutor;
import network.operationExecutor.jointOperation.UpdateNetworkObjects;
import network.operationExecutor.serverOperation.ConnectClient;
import network.receiver.UDPReceiver;
import world.World;
import world.initializer.Initializable;
import world.unit.UnitInitializer;

/**
 * represents the game server, runs a "true" copy of the game
 * and updates clients by sending relevant information
 * @author Jack
 *
 */
public final class Server implements Runnable, ConnectionManager
{
	private ServerSocket ss;
	private Map<InetAddress, Connection> connections = Collections.synchronizedMap(new HashMap<InetAddress, Connection>()); //set of connected clients
	private World w;
	
	public Server()
	{
		System.out.println("starting server...");
		try
		{
			ss = new ServerSocket(IOConstants.serverPort);
			
			JFrame f = new JFrame("Server");
			f.add(new JLabel("ip = "+InetAddress.getLocalHost()));
			f.pack();
			f.setLocationRelativeTo(null);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setVisible(true);
			new Thread(this).start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * gets the completed connections to the server
	 */
	public Map<InetAddress, Connection> getConnections()
	{
		return connections;
	}
	private void initiateServer()
	{
		OperationExecutor exe = new OperationExecutor();
		exe.registerOperation(new ConnectClient(w));
		exe.registerOperation(new UpdateNetworkObjects(w));

		DatagramSocket ds = null;
		try
		{
			ds = new DatagramSocket(IOConstants.serverPort);
		}
		catch(IOException e){}
		new UDPReceiver(ds, exe, this);
		new TCPClientConnectThread(this, ss, ds, exe);
	}
	public void run()
	{
		w = new World();
		for(int i = 0; i < 1; i++)
		{
			Initializable temp = UnitInitializer.createInitializer(w.generateNewID(), Byte.MIN_VALUE, Math.random()*400, Math.random()*400, (short)15);
			w.initialize(temp);
		}
		
		initiateServer();
		
		long sleepTime = 30;
		long start = System.currentTimeMillis();
		long updates = 0;
		for(;;)
		{
			long tdiff = System.currentTimeMillis()-start;
			start = System.currentTimeMillis();
			w.updateWorld(tdiff/1000.);
			
			long updateDiff = System.currentTimeMillis()-start;
			if(sleepTime-updateDiff > 0)
			{
				try
				{
					Thread.sleep(sleepTime-updateDiff);
				}
				catch(InterruptedException e){}
			}
			
			updates++;
			if(updates % 1000 == 0)
			{
				//exe.printExecutionTimes();
				//System.out.println("---------------------------------");
			}
		}
	}
	public static void main(String[] args)
	{
		new Server();
	}
}