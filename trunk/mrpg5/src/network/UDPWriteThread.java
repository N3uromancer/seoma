package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * writes a packet to a specified socket
 * @author Jack
 *
 */
public final class UDPWriteThread implements Runnable
{
	private byte[] buff;
	private DatagramSocket socket;
	private InetAddress address;
	private boolean fromServer;
	
	/**
	 * crerates a new write thread
	 * @param buff the buffer to be written to the other clients
	 * @param socket the socket that the packets will be written to
	 * @param address the address the packet is to be sent to
	 * @param fromServer true if the message originates from the server
	 */
	public UDPWriteThread(byte[] buff, DatagramSocket socket, InetAddress address, boolean fromServer)
	{
		this.buff = buff;
		this.socket = socket;
		this.address = address;
		this.fromServer = fromServer;
		new Thread(this).start();
	}
	public void run()
	{
		try
		{
			//long start = System.currentTimeMillis();
			
			int port = IOConstants.clientPort;
			if(!fromServer)
			{
				port = IOConstants.serverPort;
			}
			
			DatagramPacket packet = new DatagramPacket(buff, buff.length, address, port);
			socket.send(packet);
			//long diff = System.currentTimeMillis()-start;
			//System.out.println("send time (ms) = "+diff);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
