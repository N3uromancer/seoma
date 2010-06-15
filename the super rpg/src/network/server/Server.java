package network.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.OperationExecutor;
import network.operationExecutor.serverOperation.ConnectClient;
import world.World;
import world.unit.Unit;

/**
 * represents the game server, runs a "true" copy of the game
 * and updates clients by sending relevant information
 * @author Jack
 *
 */
public final class Server implements Runnable
{
	private ServerSocket ss;
	private HashMap<InetAddress, Connection> connections = new HashMap<InetAddress, Connection>(); //set of connected clients
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
	 * gets the connection that is associated with the passed address
	 * @param address
	 * @return returns the connection mapped to the passed address
	 */
	public Connection getConnection(InetAddress address)
	{
		return connections.get(address);
	}
	/**
	 * adds a connection to the server
	 * @param c
	 */
	public void addConnection(Connection c)
	{
		connections.put(c.getAddress(), c);
	}
	private void initiateServer()
	{
		OperationExecutor exe = new OperationExecutor();
		exe.registerOperation(new ConnectClient(w));
		
		new TCPClientConnectThread(this, ss, exe);
	}
	public void run()
	{
		w = new World();
		for(int i = 0; i < 3; i++)
		{
			Unit u = new Unit(false, w.generateNewID(), (byte)5, new double[]{Math.random()*400, Math.random()*400}, (short)15);
			w.registerObject(Byte.MIN_VALUE, u);
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
			//System.out.println("world updated");
			
			//writeUpdates();
			
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