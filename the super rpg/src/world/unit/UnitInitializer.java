package world.unit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import world.World;
import world.initializer.Initializable;
import world.networkUpdateable.NetworkUpdateable;

/**
 * defines a class to initialize a single unit
 * @author Jack
 *
 */
public final class UnitInitializer implements Initializable
{
	short unitID;
	byte regionID;
	byte unitType;
	boolean isGhost;
	double x;
	double y;
	boolean server;
	
	public UnitInitializer(byte[] args)
	{
		if(args.length == 21)
		{
			server = true;
			ByteBuffer b = ByteBuffer.wrap(args);
			unitID = b.getShort();
			regionID = b.get();
			unitType = b.get();
			isGhost = b.get()==1;
			x = b.getDouble();
			y = b.getDouble();
		}
		else
		{
			server = false;
			ByteBuffer b = ByteBuffer.wrap(args);
			unitID = b.getShort();
			regionID = b.get();
			unitType = b.get();
		}
	}
	public HashMap<NetworkUpdateable, Byte> initialize(World w)
	{
		HashMap<NetworkUpdateable, Byte> m = new HashMap<NetworkUpdateable, Byte>();
		if(server)
		{
			//unit created on server, isGhost value there for avatar creation
			Unit u = UnitLoader.createUnit(unitType, isGhost, unitID, new double[]{x, y});
			m.put(u, regionID);
		}
		else
		{
			//unit created on client, all units created in this manner are ghosts
			Unit u = UnitLoader.createUnit(unitType, true, unitID);
			m.put(u, regionID);
		}
		return m;
	}
	public boolean isRelevant(short id, World w)
	{
		//never called for this initializer
		return false;
	}
	public boolean immediatelyRelevant(short id, World w)
	{
		//never called for this initializer
		return false;
	}
	public boolean broadcast()
	{
		return false;
	}
	/**
	 * a convenience method for compiling a data packet to create a unit, the packet created is for
	 * creating units on the server although it can be used to create units on a client
	 * @param unitType the type of unit to be created
	 * @param isGhost
	 * @param unitID
	 * @param regionID
	 * @param x
	 * @param y
	 * @return returns the properly formatted initializer
	 */
	public static UnitInitializer createInitializer(byte unitType, boolean isGhost, short unitID, byte regionID, double x, double y)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.writeShort(unitID);
			dos.write(regionID);
			dos.write(unitType);
			dos.writeBoolean(isGhost);
			dos.writeDouble(x);
			dos.writeDouble(y);
		}
		catch(IOException e){}
		return new UnitInitializer(baos.toByteArray());
	}
	public byte[] getIniArgs()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.writeShort(unitID);
			dos.write(regionID);
			dos.write(unitType);
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
