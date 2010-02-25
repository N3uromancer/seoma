package network.client.input;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.HashMap;

import network.Operation;

import world.World;

/**
 * receives data from the server for the client
 * @author Jack
 *
 */
public final class ClientUDPReceiverThread implements Runnable
{
	private World w;
	private MulticastSocket ms;
	private boolean disconnected = false;
	
	/**
	 * the map that determines which values to throw away
	 */
	HashMap<Long, Integer> m = new HashMap<Long, Integer>();
	
	/**
	 * creates a new thread ready to receive input
	 * @param w
	 * @param ms the input stream from the socket that connects
	 * the client to the server
	 */
	public ClientUDPReceiverThread(World w, MulticastSocket ms)
	{
		this.w = w;
		this.ms = ms;
	}
	public void run()
	{
		try
		{
			while(!disconnected)
			{
				byte[] buff = new byte[1024];
				DatagramPacket temp = new DatagramPacket(buff, buff.length);
				ms.receive(temp);
				
				ByteBuffer bb = ByteBuffer.wrap(buff);
				byte operation = bb.get();
				if(operation == Operation.objectPosUpdate)
				{
					//culls packets that contain old positions
					long id = bb.getLong();
					int t = bb.getInt();
					
					if(t > m.get(id))
					{
						m.put(id, t);
						new Thread(new ClientOperation(ByteBuffer.wrap(buff), w)).start();
					}
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("terminating receiver thread");
	}
	public boolean isDisconnected()
	{
		return disconnected;
	}
}
