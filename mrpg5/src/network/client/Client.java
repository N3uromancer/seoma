package network.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import network.IOConstants;
import network.UDPReceiver;
import network.operationExecutor.OperationExecutor;
import network.operationExecutor.UpdateWorld;
import network.operationExecutor.clientOperation.ControllerSetup;
import world.World;
import display.Display;

public final class Client implements Runnable
{
	World w;
	ConnectThread ct;
	OperationExecutor exe;
	
	public Client(boolean isHost, InetAddress hostAddress) throws IOException
	{
		/*if(isHost)
		{
			new Server();
			System.out.println("client is host, server created");
		}*/
		
		//DatagramSocket socket = new DatagramSocket(IOConstants.port, InetAddress.getLocalHost());
		DatagramSocket socket = new DatagramSocket(IOConstants.clientPort); //cannot have same port as server if host
		//DatagramSocket socket = new DatagramSocket();
		System.out.println("udp socket created");
		
		w = new World();
		System.out.println("world created");
		
		exe = new OperationExecutor();
		exe.registerOperation(new ControllerSetup(w, this));
		exe.registerOperation(new UpdateWorld(w));
		System.out.println("operation executor created");
		
		new UDPReceiver(socket, exe);
		System.out.println("udp packet receiver created");
		
		System.out.println("starting connect thread");
		ct = new ConnectThread(socket, hostAddress);
	}
	/**
	 * called by the operation executor after the client connects to the server,
	 * starts the main game update thread and stops the connect thread
	 */
	public void connected()
	{
		System.out.println("client connected!");
		ct.connected();
		new Thread(this).start();
	}
	public void run()
	{
		System.out.println("initiating client main loop...");
		
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
