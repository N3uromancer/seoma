package network.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.OperationExecutor;
import network.operationExecutor.clientOperation.ControllerSetup;
import network.receiver.tcpStreamConverter.ControllerSetupConverter;
import network.receiver.tcpStreamConverter.TCPStreamConverter;
import world.World;
import display.Display;

public final class Client implements Runnable
{
	private Socket tcpSocket;
	private DatagramSocket udpSocket;
	
	private World w;
	private Connection c;
	private OperationExecutor exe;
	
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
		System.out.println("operation executor created");
		
		
		connectToServer();
	}
	/**
	 * connects the client to the server
	 */
	private void connectToServer()
	{
		TCPStreamConverter[] converters = new TCPStreamConverter[]{
				new ControllerSetupConverter()
		};
		c = new Connection(tcpSocket, udpSocket, IOConstants.serverPort, exe, converters);
		
		System.out.println("requesting game information...");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(IOConstants.connectClient);
		c.write(baos.toByteArray(), true);
	}
	/**
	 * called by the operation executor after the client connects to the server,
	 * starts the main game update thread
	 */
	public void connected()
	{
		System.out.println("client connected!");
		new Thread(this).start();
	}
	public void run()
	{
		System.out.println("initiating client main loop...");
		
		Display display = new Display(w, null);
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
				exe.printExecutionTimes();
				System.out.println("-----------------------------");
			}
		}
	}
	public static void main(String[] args)
	{
		try
		{
			new Client(true, InetAddress.getLocalHost());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
