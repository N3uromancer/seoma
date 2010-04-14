package network.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import network.IOConstants;

/**
 * connects the client to the server, sends packets with the connect
 * operation id at regular intervals to the server
 * @author Jack
 *
 */
public class ConnectThread implements Runnable
{
	DatagramSocket socket;
	InetAddress host;
	boolean connected = false;
	
	public ConnectThread(DatagramSocket socket, InetAddress host)
	{
		this.socket = socket;
		this.host = host;
		new Thread(this).start();
	}
	public void run()
	{
		byte[] buff = {IOConstants.connectClient};
		DatagramPacket packet = new DatagramPacket(buff, buff.length, host, IOConstants.serverPort);
		
		System.out.println("connecting to server...");
		try
		{
			for(int i = 0; i < 10 && !connected; i++)
			{
				socket.send(packet);
				System.out.println("requesting...");
				try
				{
					Thread.sleep(500);
				}
				catch(InterruptedException e){}
			}
		}
		catch(IOException e){}
		if(!connected)
		{
			System.out.println("connection FAILURE");
			System.out.println("exiting");
			System.exit(0);
		}
		else
		{
			System.out.println("connection SUCCESSFUL!");
		}
	}
	/**
	 * called to notify the connect thread that the client has connected successfully
	 */
	public void connected()
	{
		connected = true;
		System.out.println("connect thread stopped");
	}
}
