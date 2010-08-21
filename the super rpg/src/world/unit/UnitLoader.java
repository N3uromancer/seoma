package world.unit;

import java.io.File;
import java.util.HashMap;

import world.unit.script.WanderScript;

/**
 * defines a unit loader that loads units from the unit data file, the only
 * exception is the avatar unit which is loaded from a class file
 *
 */
public final class UnitLoader
{
	private static HashMap<Byte, byte[]> unitTypeID = new HashMap<Byte, byte[]>();

	static
	{
		File unitData;
		System.out.print("loading unit data... ");
		
		System.out.println("done");
	}
	public static Unit createUnit(byte type, boolean isGhost, short id, double[] l)
	{
		if(type == Byte.MIN_VALUE)
		{
			//avatar
			return new Unit(isGhost, id, l, (short)15);
		}
		else
		{
			//normal unit
			Unit u = new Unit(isGhost, id, l, (short)10);
			u.setScript(new WanderScript());
			return u;
		}
	}
	public static Unit createUnit(byte type, boolean isGhost, short id)
	{
		if(type == Byte.MIN_VALUE)
		{
			return new Unit(isGhost, id, (short)15);
		}
		else
		{
			return new Unit(isGhost, id, (short)10);
		}
	}
}
