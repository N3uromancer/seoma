package world.unit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

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
	}
	public void initialize(World w)
	{
		if(server)
		{
			//unit created on server
			NetworkUpdateable u = new Unit(false, unitID, new double[]{x, y}, radius);
			w.registerObject(regionID, u, this);
			
		}
		else
		{
			//unit created on client
			NetworkUpdateable u = new Unit(true, unitID, radius);
			w.registerObject(regionID, u, this);
		}
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
	 * @return returns the properly formatted data buffer to initialize a unit
	 */
	public static byte[] createDataBuffer(short unitID, byte regionID, double x, double y, short radius)
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
		return baos.toByteArray();
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
