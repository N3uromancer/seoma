package network.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import network.Connection;
import network.ConnectionManager;
import network.IOConstants;
import network.operationExecutor.OperationExecutor;
import network.operationExecutor.clientOperation.ControllerSetup;
import network.operationExecutor.clientOperation.DestroyObject;
import network.operationExecutor.jointOperation.PerformInitialization;
import network.operationExecutor.jointOperation.UpdateNetworkObjects;
import network.receiver.UDPReceiver;
import world.World;
import display.Display;

public final class Client implements Runnable, ConnectionManager
{
	private Socket tcpSocket;
	private DatagramSocket udpSocket;
	private Map<InetAddress, Connection> connections = Collections.synchronizedMap(new HashMap<InetAddress, Connection>());
	
	private World w;
	private Connection c;
	private OperationExecutor exe;
	private short avatar; //the client's avatar in the game world
	
	/**
	 * creates a new client
	 * @param isHost
	 * @param hostAddress the address of the computer that is running the server
	 * @throws IOException
	 */
	public Client(boolean isHost, InetAddress hostAddress) throws IOException
	{
		System.out.println("starting client...");
		tcpSocket = new Socket(hostAddress, IOConstants.serverPort);
		System.out.println("tcp socket created");
		udpSocket = new DatagramSocket(IOConstants.clientPort);
		System.out.println("udp socket created");
		
		w = new World();
		System.out.println("world created");
		
		exe = new OperationExecutor();
		exe.registerOperation(new ControllerSetup(w, this));
		exe.registerOperation(new UpdateNetworkObjects(w));
		exe.registerOperation(new DestroyObject(w));
		exe.registerOperation(new PerformInitialization(w));
		System.out.println("operation executor created");

		new UDPReceiver(udpSocket, exe, this);
		
		connectToServer();
	}
	public Map<InetAddress, Connection> getConnections()
	{
		return connections;
	}
	/**
	 * connects the client to the server
	 */
	private void connectToServer()
	{
		c = new Connection(tcpSocket, udpSocket, IOConstants.serverPort, exe);
		
		System.out.println("requesting game information...");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(IOConstants.connectClient);
		c.write(baos.toByteArray(), true);
	}
	/**
	 * called by the operation executor after the client connects to the server,
	 * starts the main game update thread
	 */
	public void connected(short avatarID)
	{
		System.out.println("client connected!");
		this.avatar = avatarID;
		new Thread(this).start();
	}
	public void run()
	{
		System.out.println("initiating client main loop...");
		short avatarUpdateIndex = Short.MIN_VALUE;
		
		Display display = new Display(w, w.getController());
		long sleepTime = 30;
		long start = System.currentTimeMillis();
		long updates = 0;
		for(;;)
		{
			long tdiff = System.currentTimeMillis()-start;
			start = System.currentTimeMillis();
			w.updateWorld(tdiff/1000.);
			display.drawWorld();
			
			//writeUpdates((short)updates);
			
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
			if(updates % 200 == 0)
			{
				//exe.printExecutionTimes();
				//System.out.println("-----------------------------");
			}
			
			updateAvatar(avatarUpdateIndex);
			avatarUpdateIndex++;
		}
	}
	/**
	 * updates the avatar on the server
	 */
	private void updateAvatar(short updateIndex)
	{
		if(w.getAssociatedRegion(avatar) != null)
		{
			//avatar has been initialized within the world
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			try
			{
				dos.write(IOConstants.updateNetworkObjects);
				dos.writeShort(updateIndex);
				dos.writeByte(Byte.MIN_VALUE+1);
				dos.writeShort(avatar);
				byte[] state = w.getAssociatedRegion(avatar).getState(avatar);
				dos.write(state.length-128);
				dos.write(state);
			}
			catch(IOException e){}
			c.write(baos.toByteArray(), false);
		}
	}
	public static void main(String[] args)
	{
		try
		{
			//System.out.println(InetAddress.getLocalHost());
			//System.out.println(InetAddress.getByName("QAYPN2"));
			
			//new Client(true, InetAddress.getByName("QAYPN2"));
			//new Client(true, InetAddress.getLocalHost());
			new Client(true, InetAddress.getByName("99.187.186.83")); //kyle's house ip
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
