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
	public byte[] initialize(byte[] args, World w)
	{
		if(args.length == 21)
		{
			//unit created on server
			ByteBuffer b = ByteBuffer.wrap(args);
			short unitID = b.getShort();
			byte regionID = b.get();
			double x = b.getDouble();
			double y = b.getDouble();
			short radius = b.getShort(); //here there should be instead a byte for unit type to be loaded by the unit loader
			NetworkUpdateable u = new Unit(false, unitID, new double[]{x, y}, radius);
			w.registerObject(regionID, u);
			
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
		else
		{
			//unit created on client
			ByteBuffer b = ByteBuffer.wrap(args);
			short unitID = b.getShort();
			byte regionID = b.get();
			short radius = b.getShort(); //here there should be instead a byte for unit type to be loaded by the unit loader
			NetworkUpdateable u = new Unit(true, unitID, radius);
			w.registerObject(regionID, u);
			return args;
		}
	}
	public boolean isRelevant(short id, byte[] args, World w)
	{
		ByteBuffer b = ByteBuffer.wrap(args);
		short unitID = b.getShort();
		return !w.isDestroyed(unitID);
	}
	public boolean immediatelyRelevant(short id, byte[] args, World w)
	{
		ByteBuffer b = ByteBuffer.wrap(args);
		short unitID = b.getShort();
		return w.getAssociatedRegion(id) == w.getAssociatedRegion(unitID);
	}
	public boolean broadcast()
	{
		return true;
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
}
