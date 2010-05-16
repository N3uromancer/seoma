package network.receiver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import network.Connection;
import network.operationExecutor.OperationExecutor;
import network.receiver.tcpStreamConverter.TCPStreamConverter;

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
	private HashMap<Byte, TCPStreamConverter> c = new HashMap<Byte, TCPStreamConverter>();
	
	/**
	 * creates a new udp packet receiver
	 * @param socket
	 * @param oe
	 * @param converters the converters to be registered with this tcp receiver
	 * @param connection the connection this receiver is part of
	 */
	public TCPReceiver(Socket socket, OperationExecutor oe, TCPStreamConverter[] converters, Connection connection)
	{
		this.socket = socket;
		this.oe = oe;
		this.connection = connection;
		
		for(TCPStreamConverter converter: converters)
		{
			c.put(converter.getID(), converter);
		}
		
		/*Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();*/
		
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
				byte id = dis.readByte();
				if(!c.containsKey(id))
				{
					System.err.println("a tcp converter with id="+id+" does not exist");
					System.exit(0);
				}
				byte[] buff = c.get(id).convertStream(dis);
				oe.add(buff, connection);
			}
		}
		catch(IOException e)
		{
			//e.printStackTrace();
			System.out.println("disconnect detected in tcp receiver");
			connection.closeConnection();
		}
	}
}
