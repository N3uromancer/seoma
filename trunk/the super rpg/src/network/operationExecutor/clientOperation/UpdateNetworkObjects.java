package network.operationExecutor.clientOperation;

import java.nio.ByteBuffer;

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
	private short updateIndex = Short.MIN_VALUE;
	
	public UpdateNetworkObjects(World w)
	{
		super(IOConstants.updateNetworkObjects);
		this.w = w;
	}
	public void performOperation(ByteBuffer buff, Connection c)
	{
		short index = buff.getShort();
		if(index >= updateIndex || index < updateIndex-20000) //second boolean for if it rolls over
		{
			//System.out.println("======== updating network objects ========");
			updateIndex = index;
			byte length = buff.get();
			//System.out.println("objects to udpate = "+(length+128));
			for(int i = Byte.MIN_VALUE; i < length; i++)
			{
				short id = buff.getShort();
				//System.out.println("id="+id+" updated");
				byte dataLength = (byte)(buff.get()+128);
				//System.out.println("data length = "+dataLength);
				byte[] data = new byte[dataLength];
				for(int a = 0; a < dataLength; a++)
				{
					data[a] = buff.get();
				}
				w.updateNetworkObject(id, data);
			}
		}
	}
}
