package network.receiver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import network.Connection;
import network.operationExecutor.OperationExecutor;

/**
 * receives packets, translates the stream to a readable byte buffer, and forwards
 * the buffer to the operation executor
 * @author Jack
 *
 */
public final class TCPReceiver extends Thread
{
	private Socket socket;
	private OperationExecutor oe;
	private Connection connection;
	
	/**
	 * creates a new udp packet receiver
	 * @param socket
	 * @param oe
	 * @param connection the connection this receiver is part of
	 */
	public TCPReceiver(Socket socket, OperationExecutor oe, Connection connection)
	{
		this.socket = socket;
		this.oe = oe;
		this.connection = connection;
		
		setDaemon(true);
		start();
	}
	public void run()
	{
		//System.out.println("tcp receiver started");
		try
		{
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			for(;;)
			{
				short length = dis.readShort();
				byte[] buff = new byte[length-Short.MIN_VALUE];
				dis.read(buff);
				oe.add(buff, connection);
			}
		}
		catch(IOException e)
		{
			System.out.println("disconnect detected in tcp receiver");
			connection.closeConnection();
		}
	}
}
