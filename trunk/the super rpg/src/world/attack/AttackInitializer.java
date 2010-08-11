package world.attack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import world.World;
import world.initializer.Initializable;
import world.networkUpdateable.NetworkUpdateable;
import world.region.Region;
import world.unit.Unit;

public final class AttackInitializer implements Initializable
{
	boolean isGhost;
	short id;
	short unitID;
	byte direction;
	
	boolean assignID = false; //true if initialization order came from a client which cant assign an id
	
	byte[] args;
	
	public AttackInitializer(byte[] args)
	{
		this.args = args;
		
		ByteBuffer b = ByteBuffer.wrap(args);
		if(args.length == 6)
		{
			isGhost = b.get()==1;
			id = b.getShort();
			unitID = b.getShort();
			direction = b.get();
		}
		else
		{
			//arg length = 4
			isGhost = b.get()==1;
			unitID = b.getShort();
			direction = b.get();
			assignID = true;
		}
	}
	public byte[] getIniArgs()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.writeBoolean(true);
			dos.writeShort(id);
			dos.writeShort(unitID);
			dos.write(direction);
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
	public byte[] getOriginalIniArgs()
	{
		return args;
	}
	public HashMap<NetworkUpdateable, Byte> initialize(World w)
	{
		HashMap<NetworkUpdateable, Byte> m = new HashMap<NetworkUpdateable, Byte>();
		
		Unit unit = null;
		try
		{
			Region r = w.getAssociatedRegion(unitID);
			r.getSemaphore().acquire();
			unit = (Unit)r.getNetworkObject(unitID);
			r.getSemaphore().release();
		}
		catch(InterruptedException e){}
		
		if(assignID)
		{
			id = w.generateNewID();
		}
		MeleeAttack attack = new MeleeAttack(isGhost, id, unit, direction);
		m.put(attack, w.getAssociatedRegion(unitID).getRegionID());
		
		return m;
	}
	public static byte[] createAttack(boolean isGhost, short id, short unitID, byte direction)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.writeBoolean(isGhost);
			dos.writeShort(id);
			dos.writeShort(unitID);
			dos.write(direction);
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
	/**
	 * creates a new attack on the client initialized by a client avatar, new
	 * attacks are created as non ghost objects and are flagged to be dynamically
	 * assigned an id by the server upon their initialization
	 * @param unitID
	 * @param direction
	 * @return
	 */
	public static byte[] createAttack(short unitID, byte direction)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.writeBoolean(false);
			dos.writeShort(unitID);
			dos.write(direction);
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
