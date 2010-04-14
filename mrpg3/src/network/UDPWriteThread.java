package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * writes a packet to the other clients
 * @author Jack
 *
 */
public class UDPWriteThread implements Runnable
{
	byte[] buff;
	MulticastSocket ms;
	
	/**
	 * crerates a new write thread
	 * @param buff the buffer to be written to the other clients
	 * @param ms
	 */
	public UDPWriteThread(byte[] buff, MulticastSocket ms)
	{
		this.buff = buff;
		this.ms = ms;
		new Thread(this).start();
	}
	public void run()
	{
		try
		{
			DatagramPacket packet = new DatagramPacket(buff, buff.length, 
					InetAddress.getByName("230.0.0.1"), IOConstants.port);
			ms.send(packet);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
