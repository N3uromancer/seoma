package network.operationExecutor;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

import network.IOConstants;
import world.World;
import world.unit.StillUnit;

/**
 * represents an operation that updates a game world
 * @author Jack
 *
 */
public final class UpdateWorld extends Operation
{
	private World w;
	private HashMap<String, Short> updateCounts = new HashMap<String, Short>();
	
	/**
	 * creates a new update world operation
	 * @param w the associated world to be updated
	 */
	public UpdateWorld(World w)
	{
		super(IOConstants.updateWorld);
		this.w = w;
	}
	public void performOperation(ByteBuffer buff, InetAddress address)
	{
		short updateCount = buff.getShort();
		if(!updateCounts.containsKey(address) || updateCount > updateCounts.get(address.getHostAddress()))
		{
			//System.out.println("updating world...");
			updateCounts.put(address.getHostName(), updateCount);
			byte length = buff.get();
			//System.out.println("objects to update = "+(length-Byte.MIN_VALUE));
			short id;
			for(byte i = Byte.MIN_VALUE; i < length; i++)
			{
				id = buff.getShort();
				if(w.getObject(id) == null)
				{
					//NEEDS SOME WAY TO DETERMINE THE CORRECT TYPE OF OBJECT TO CREATE AND THE REGION IT BELONGS TO
					w.registerObject(new StillUnit(new double[]{50, 50}), id, (byte)0);
					//System.out.println("id "+id+" not registered, unit created");
				}
				w.getObject(id).readState(buff);
				//System.out.println("state read, id = "+id);
			}
		}
	}
}
