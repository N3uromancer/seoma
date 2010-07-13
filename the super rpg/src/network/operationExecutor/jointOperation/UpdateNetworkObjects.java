package network.operationExecutor.jointOperation;

import java.nio.ByteBuffer;
import java.util.HashMap;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.Operation;
import world.World;

/**
 * used to update network objects, does most of its work when clients
 * receive updates from the server although also used to update client
 * avatars on the server
 * @author Jack
 *
 */
public final class UpdateNetworkObjects extends Operation
{
	private World w;
	private HashMap<Connection, Short> indeces = new HashMap<Connection, Short>(); //update indeces, on a per connection basic
	
	public UpdateNetworkObjects(World w)
	{
		super(IOConstants.updateNetworkObjects);
		this.w = w;
	}
	public void performOperation(ByteBuffer buff, Connection c)
	{
		//System.out.println("======== updating network objects ========");
		//System.out.println("checking to perform network object update, c="+c);
		short index = buff.getShort();
		//System.out.println("index="+index+", old index="+indeces.get(c));
		byte length = buff.get();
		//System.out.println("objects to udpate = "+(length+128));
		for(int i = Byte.MIN_VALUE; i < length; i++)
		{
			short id = buff.getShort();
			byte dataLength = (byte)(buff.get()+128);
			//System.out.println("data length = "+dataLength);
			byte[] data = new byte[dataLength];
			for(int a = 0; a < dataLength; a++)
			{
				data[a] = buff.get();
			}
			if(indeces.get(c) == null || index >= indeces.get(c) || index < indeces.get(c)-20000) //third boolean for if it rolls over
			{
				//System.out.println("id="+id+" updated");
				w.updateNetworkObject(id, data);
			}
		}
		if(indeces.get(c) == null || index >= indeces.get(c) || index < indeces.get(c)-20000) //third boolean for if it rolls over
		{
			indeces.put(c, index);
		}
	}
}
