package network.writeController;

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
	private int port;
	
	/**
	 * crerates a new write thread
	 * @param buff the buffer to be written to the other clients
	 * @param socket the socket that the packets will be written to
	 * @param address the address the packet is to be sent to
	 * @param port the port the buffer is to be written to
	 */
	public UDPWriteThread(byte[] buff, DatagramSocket socket, InetAddress address, int port)
	{
		this.buff = buff;
		this.socket = socket;
		this.address = address;
		this.port = port;
		new Thread(this).start();
	}
	public void run()
	{
		try
		{
			//long start = System.currentTimeMillis();
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
