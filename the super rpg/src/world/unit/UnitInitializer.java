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
	double x;
	double y;
	short radius;
	boolean server;
	
	public UnitInitializer(byte[] args)
	{
		if(args.length == 21)
		{
			server = true;
			ByteBuffer b = ByteBuffer.wrap(args);
			unitID = b.getShort();
			regionID = b.get();
			x = b.getDouble();
			y = b.getDouble();
			radius = b.getShort();
		}
		else
		{
			server = false;
			ByteBuffer b = ByteBuffer.wrap(args);
			unitID = b.getShort();
			regionID = b.get();
			radius = b.getShort();
		}
	}
	public HashMap<NetworkUpdateable, Byte> initialize(World w)
	{
		HashMap<NetworkUpdateable, Byte> m = new HashMap<NetworkUpdateable, Byte>();
		if(server)
		{
			//unit created on server
			NetworkUpdateable u = new Unit(false, unitID, new double[]{x, y}, radius);
			m.put(u, regionID);
		}
		else
		{
			//unit created on client
			NetworkUpdateable u = new Unit(true, unitID, radius);
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
	 * @param unitID
	 * @param regionID
	 * @param x
	 * @param y
	 * @param radius
	 * @return returns the properly formatted initializer
	 */
	public static UnitInitializer createInitializer(short unitID, byte regionID, double x, double y, short radius)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.writeShort(unitID);
			dos.write(regionID);
			dos.writeDouble(x);
			dos.writeDouble(y);
			dos.writeShort(radius); //should write unit type here
		}
		catch(IOException e){}
		return new UnitInitializer(baos.toByteArray());
	}
	public static UnitInitializer createInitializer(Unit u, byte regionID)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.writeShort(u.getID());
			dos.write(regionID);
			dos.writeDouble(u.getLocation()[0]);
			dos.writeDouble(u.getLocation()[1]);
			dos.writeShort(u.getRadius()); //should write unit type here
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
			dos.writeShort(radius); //should write unit type here
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
