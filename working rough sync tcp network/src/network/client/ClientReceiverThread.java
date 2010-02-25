package network.client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import network.Operation;
import world.World;

/**
 * receives data from the server for the client
 * @author Jack
 *
 */
public final class ClientReceiverThread implements Runnable
{
	private World w;
	private DataInputStream dis;
	private boolean disconnected = false;
	
	/**
	 * creates a new thread ready to receive input
	 * @param w
	 * @param dis the input stream from the socket that connects
	 * the client to the server
	 */
	public ClientReceiverThread(World w, DataInputStream dis)
	{
		this.w = w;
		this.dis = dis;
	}
	public void run()
	{
		try
		{
			while(!disconnected)
			{
				byte operation = dis.readByte();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				dos.write(operation);
				
				if(operation == Operation.objectPosUpdate)
				{
					
					long id = dis.readLong();
					int x = dis.readInt();
					int y = dis.readInt();
					
					//System.out.println(id+", "+x+", "+y);

					dos.writeLong(id);
					dos.writeInt(x);
					dos.writeInt(y);
					new Thread(new ClientOperation(ByteBuffer.wrap(baos.toByteArray()), w)).start();
				}
				else if(operation == Operation.objectRemove)
				{

					long id = dis.readLong();
					dos.writeLong(id);
					new Thread(new ClientOperation(ByteBuffer.wrap(baos.toByteArray()), w)).start();
				}
				else if(operation == Operation.serverDisconnect)
				{
					disconnected = true;
					System.out.println("disconnect message received from server");
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
