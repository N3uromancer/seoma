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
import world.unit.Unit;

/**
 * an operation for spawning units, used by the client to interpret
 * spawn orders from the server
 * @author Jack
 *
 */
public final class SpawnUnit extends Operation
{
	private World w;
	private UnitFactory f;
	
	public SpawnUnit(World w)
	{
		super(IOConstants.spawnUnit);
		this.w = w;
		f = new UnitFactory("example unit stats.txt");
	}
	public void performOperation(ByteBuffer buff, Connection c)
	{
		byte length = buff.get();
		for(int i = 0; i < length; i++)
		{
			byte type = buff.get();
			byte regionID = buff.get();
			short objID = buff.getShort();
			
			Unit u = f.createUnit(type, objID);
			w.registerObject(regionID, u);
		}
	}
	public static byte[] createByteBuffer(ArrayList<NetworkUpdateable> u, World w)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.spawnUnit);
			for(NetworkUpdateable o: u)
			{
				if(o instanceof Unit)
				{
					dos.write(o.getType());
					baos.write(w.getAssociatedRegion(o.getID()).getID());
					dos.writeShort(o.getID());
				}
			}
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
