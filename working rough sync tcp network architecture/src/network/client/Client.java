package network.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import network.Operation;

import world.World;
import display.WorldDisplay;

/**
 * represents a client
 * @author Jack
 *
 */
public class Client
{
	Socket s;
	/**
	 * if false the main loop of the program continues
	 */
	boolean done = false;
	/**
	 * true if the client should disconnect
	 */
	boolean disconnect = false;
	
	/**
	 * creates a new client and connects it to the server
	 * @param port
	 * @param host
	 */
	public Client(int port, String host)
	{
		try
		{
			s = new Socket(host, port);
			System.out.println("client connected to server");
			
			updateClient();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("exiting");
	}
	/**
	 * runs and updates the client
	 * @throws IOException
	 */
	private void updateClient() throws IOException
	{
		DataInputStream dis = new DataInputStream(s.getInputStream());
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		System.out.println("io streams created");
		
		//reads the range of ids the client has access to
		long idStart = dis.readLong();
		long idEnd = dis.readLong();
		System.out.println("client id range: "+idStart+" - "+idEnd);
		
		World w = new World(idStart, idEnd, this);
		System.out.println("world created");
		WorldDisplay display = new WorldDisplay(w);
		System.out.println("world display created");
		
		ClientReceiverThread crt = new ClientReceiverThread(w, dis);
		new Thread(crt).start();
		System.out.println("client receiver thread started");
		
		ClientWriterThread cwt = new ClientWriterThread(dos);
		new Thread(cwt).start();
		System.out.println("client writer thread started");
		
		System.out.println("initializing main loop");
		long sleep = 30;
		System.out.println("sleep time (ms): "+sleep);
		long start;
		long diff;
		while(!done)
		{
			if(disconnect && !cwt.isDisconnected())
			{
				cwt.disconnect();
			}
			else if(disconnect && cwt.isDisconnected())
			{
				done = true;
				System.out.println("terminating update loop");
			}
			else
			{
				start = System.currentTimeMillis();
				w.updateWorld(sleep/1000., cwt);
				display.drawWorld();
				diff = System.currentTimeMillis()-start;
				//System.out.println(diff);
				if(sleep-diff > 0)
				{
					try
					{
						Thread.sleep(sleep-diff);
					}
					catch(InterruptedException e){}
				}
			}
			
		}
		System.out.println("closing socket");
		s.close();
		System.out.println("exiting");
		System.exit(0);
	}
	/**
	 * terminates the client's connection to the server and exits
	 * the program
	 */
	public void disconnect()
	{
		System.out.println("disconnecting...");
		disconnect = true;
	}
	public static void main(String[] args)
	{
		int port = 4567;
		String host = null; //null for loopback
		new Client(port, host);
	}
}
