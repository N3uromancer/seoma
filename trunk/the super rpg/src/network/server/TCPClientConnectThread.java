package network.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.OperationExecutor;

/**
 * listens to the server socket and connects clients to the server
 * @author Jack
 *
 */
public final class TCPClientConnectThread extends Thread
{
	private Server s;
	private ServerSocket ss;
	private OperationExecutor oe;
	private DatagramSocket ds;
	
	/**
	 * creates a new connect thread to connect clients to the server
	 * @param ss
	 * @param oe
	 */
	public TCPClientConnectThread(Server s, ServerSocket ss, DatagramSocket ds, OperationExecutor oe)
	{
		this.s = s;
		this.ss = ss;
		this.ds = ds;
		this.oe = oe;
		
		setDaemon(true);
		start();
	}
	public void run()
	{
		for(;;)
		{
			try
			{
				Socket socket = ss.accept();
				System.out.println("new client connection found!");
				Connection c = new Connection(socket, ds, IOConstants.clientPort, oe);
				s.getConnections().put(c.getAddress(), c);
			}
			catch(IOException e){}
		}
	}
	/**
	 * called after the game has started and no more clients can join,
	 * essentially terminates the connect thread
	 */
	public void terminate()
	{
		interrupt();
		System.out.println("client connect thread interrupted");
	}
}
