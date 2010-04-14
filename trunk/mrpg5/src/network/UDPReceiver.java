package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import network.operationExecutor.OperationExecutor;

/**
 * receives packets and forwards them to the operation executor
 * to be carried out
 * @author Jack
 *
 */
public final class UDPReceiver implements Runnable
{
	private DatagramSocket socket;
	private OperationExecutor oe;
	
	/**
	 * creates a new udp packet receiver
	 * @param socket
	 * @param oe
	 */
	public UDPReceiver(DatagramSocket socket, OperationExecutor oe)
	{
		this.socket = socket;
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
				socket.receive(packet);
				oe.add(packet);
				//System.out.println("received! ip="+packet.getAddress().getHostAddress()+", port="+packet.getPort());
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
