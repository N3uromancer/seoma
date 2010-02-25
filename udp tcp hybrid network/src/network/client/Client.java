package network.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import network.client.input.ClientReceiverThread;
import network.client.input.ClientUDPReceiverThread;
import network.client.output.ClientTCPWriterThread;
import network.client.output.ClientUDPWriterThread;
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
			
			startClient();
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
	private void startClient() throws IOException
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

		MulticastSocket ms = new MulticastSocket(8897);
		InetAddress group = InetAddress.getByName(w.getMulticastGroup());
		ms.joinGroup(group);
		System.out.println("creating multicast socket, "+InetAddress.getByName(w.getMulticastGroup()));
		
		ClientUDPReceiverThread udpRec = new ClientUDPReceiverThread(w, ms);
		new Thread(udpRec).start();
		System.out.println("client udp receiver thread started");
		
		ClientReceiverThread crt = new ClientReceiverThread(w, dis);
		new Thread(crt).start();
		System.out.println("client receiver thread started");
		
		ClientUDPWriterThread udpWrit = new ClientUDPWriterThread(ms, group);
		new Thread(udpWrit).start();
		System.out.println("client udp writer thread started");
		
		ClientTCPWriterThread tcp = new ClientTCPWriterThread(dos);
		new Thread(tcp).start();
		System.out.println("client tcp writer thread started");
		
		System.out.println("initializing main loop");
		long sleep = 30;
		System.out.println("sleep time (ms): "+sleep);
		long start;
		long diff;
		while(!done)
		{
			if(disconnect && !tcp.isDisconnected())
			{
				tcp.disconnect();
			}
			else if(disconnect && tcp.isDisconnected())
			{
				done = true;
				System.out.println("terminating update loop");
			}
			else
			{
				start = System.currentTimeMillis();
				w.updateWorld(sleep/1000., tcp, udpWrit);
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
		//String host = "QAYPN"; //null for loopback
		String host = null;
		//String host = "user-9b72ad9";
		new Client(port, host);
	}
}
