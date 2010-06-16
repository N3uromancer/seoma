package network.receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import network.ConnectionManager;
import network.operationExecutor.OperationExecutor;

/**
 * receives packets and forwards them to the operation executor
 * to be carried out, only one udp listener is needed per machine
 * because it listens to all incoming data on a port
 * @author Jack
 *
 */
public final class UDPReceiver extends Thread
{
	private DatagramSocket socket;
	private OperationExecutor oe;
	private ConnectionManager cm;
	
	/**
	 * creates a new udp packet receiver
	 * @param socket
	 * @param oe
	 * @param cm
	 */
	public UDPReceiver(DatagramSocket socket, OperationExecutor oe, ConnectionManager cm)
	{
		this.socket = socket;
		this.oe = oe;
		this.cm = cm;
		
		setDaemon(true);
		start();
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
				oe.add(packet.getData(), cm.getConnections().get(packet.getAddress()));
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
