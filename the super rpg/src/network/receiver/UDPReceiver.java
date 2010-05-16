package network.receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import network.Connection;
import network.operationExecutor.OperationExecutor;

/**
 * receives packets and forwards them to the operation executor
 * to be carried out
 * @author Jack
 *
 */
public final class UDPReceiver extends Thread
{
	private DatagramSocket socket;
	private OperationExecutor oe;
	private Connection c;
	
	/**
	 * creates a new udp packet receiver
	 * @param socket
	 * @param oe
	 * @param connection the connection this receiver is part of
	 */
	public UDPReceiver(DatagramSocket socket, OperationExecutor oe, Connection c)
	{
		this.socket = socket;
		this.oe = oe;
		this.c = c;

		/*Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();*/
		setDaemon(true);
		start();
	}
	public void run()
	{
		//System.out.println("udp receiver started");
		try
		{
			for(;;)
			{
				byte[] buff = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buff, buff.length);
				socket.receive(packet);
				oe.add(packet.getData(), c);
				//System.out.println("received! ip="+packet.getAddress().getHostAddress()+", port="+packet.getPort());
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
