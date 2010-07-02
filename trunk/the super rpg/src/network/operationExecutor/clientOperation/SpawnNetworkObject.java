package network.operationExecutor.clientOperation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.Operation;
import world.World;
import world.factory.UnitFactory;
import world.modifier.NetworkUpdateable;
import world.modifier.ObjectType;
import world.unit.Unit;

/**
 * an operation for spawning network updateable objects, used by the client to interpret
 * spawn orders from the server
 * @author Jack
 *
 */
public final class SpawnNetworkObject extends Operation
{
	private World w;
	private UnitFactory f;
	
	public SpawnNetworkObject(World w)
	{
		super(IOConstants.spawnObject);
		this.w = w;
		f = new UnitFactory("example unit stats.txt");
	}
	public void performOperation(ByteBuffer buff, Connection c)
	{
		System.out.println("spawn unit command received from c="+c);
		byte length = buff.get();
		for(int i = Byte.MIN_VALUE; i < length; i++)
		{
			byte t = buff.get(); //type
			byte regionID = buff.get();
			short objID = buff.getShort();
			
			byte iniLength = buff.get(); //initial state buffer length
			byte[] iniState = new byte[iniLength]; //initial state buffer
			buff.get(iniState);
			
			ObjectType type = null;
			for(ObjectType temp: ObjectType.values())
			{
				if(temp.getTypeCode() == t)
				{
					type = temp;
				}
			}
			
			NetworkUpdateable u = null;
			if(type == ObjectType.unit)
			{
				u = f.createUnit(true, t, objID);
			}
			if(u != null)
			{
				u.loadInitialState(iniState, w);
				System.out.println("spawning id="+objID+", region id="+regionID+", type="+type.name());
				w.registerObject(regionID, u);
			}
			else
			{
				System.err.println("unrecognized network object type, unable to spawn object");
				System.exit(0);
			}
		}
	}
	/**
	 * a simple helper method to correctly format a byte buffer to be sent to clients by the server,
	 * the buffer contains the necesary information for clients to carry out spawn orders
	 * @param u
	 * @param w
	 * @return
	 */
	public static byte[] createByteBuffer(ArrayList<NetworkUpdateable> u, World w)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.spawnObject);
			dos.write(u.size()-128);
			for(NetworkUpdateable o: u)
			{
				if(o instanceof Unit)
				{
					dos.write(o.getType().getTypeCode());
					dos.write(w.getAssociatedRegion(o.getID()).getRegionID());
					dos.writeShort(o.getID());
					
					byte[] iniState = o.getInitialState();
					byte iniLength = iniState == null? 0: (byte)iniState.length;
					dos.write(iniLength);
					if(iniState != null)
					{
						dos.write(iniState);
					}
				}
			}
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
