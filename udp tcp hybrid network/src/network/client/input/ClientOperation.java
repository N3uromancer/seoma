package network.client.input;

import java.nio.ByteBuffer;

import world.GameObject;
import world.World;


import network.Operation;

/**
 * represents an operation received from the server, operations
 * wait until they can modify the world then they carry out their
 * specific operation, client operations are used to allow the
 * receiver thread to continually read without pausing to carry
 * out the operations it receives
 * @author Jack
 *
 */
public final class ClientOperation implements Runnable
{
	private ByteBuffer buff;
	private World w;
	
	/**
	 * creates a new client operation
	 * @param buff the buffer containing the operation to be carried
	 * out on the world
	 * @param w a refernce to the game world
	 */
	public ClientOperation(ByteBuffer buff, World w)
	{
		this.buff = buff;
		this.w = w;
	}
	public void run()
	{
		try
		{
			byte operation = buff.get();
			if(operation == Operation.objectPosUpdate)
			{
				long id = buff.getLong();
				buff.getInt(); //the count int to determine if the data is relvent
				int x = buff.getInt();
				int y = buff.getInt();
				
				w.getObjectSemaphore().acquire();
				if(w.getObjects().containsKey(id))
				{
					w.getObjects().get(id).setLocation(x, y);
				}
				else
				{
					w.getObjects().put(id, new GameObject(x, y, true, id));
				}
				w.getObjectSemaphore().release();
			}
			else if(operation == Operation.objectRemove)
			{
				long id = buff.getLong();
				w.getObjectSemaphore().acquire();
				w.getObjects().remove(id);
				w.getObjectSemaphore().release();
			}
		}
		catch(InterruptedException e){}
	}
}
