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
	
	byte[] args;
	
	public AttackInitializer(byte[] args)
	{
		this.args = args;
		
		ByteBuffer b = ByteBuffer.wrap(args);
		isGhost = b.get()==1;
		id = b.getShort();
		unitID = b.getShort();
		direction = b.get();
	}
	public byte[] getIniArgs()
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
}
