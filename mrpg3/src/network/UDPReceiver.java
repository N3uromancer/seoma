package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 * receives packets and forwards them to the operation executor
 * to be carried out
 * @author Jack
 *
 */
public class UDPReceiver implements Runnable
{
	MulticastSocket ms;
	OperationExecutor oe;
	
	public UDPReceiver(MulticastSocket ms, OperationExecutor oe)
	{
		this.ms = ms;
		this.oe = oe;
		new Thread(this).start();
	}
	public void run()
	{
		try
		{
			for(;;)
			{
				byte[] buff = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buff, buff.length);
				ms.receive(packet);
				if(packet.getAddress().getHostAddress() != ms.getLocalAddress().getHostAddress())
				{
					//packet is not from this client
					oe.add(packet);
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
