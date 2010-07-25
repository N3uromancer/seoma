package world.unit;

import java.io.File;
import java.util.HashMap;

/**
 * defines a unit loader that loads unit either from specified registered classes
 * or from a data file containing unit stats, in units created from classs the
 * actual class is used to create an object, in units created from the data file
 * the standard unit class is used with the arguments specified in the file
 * @author Jack
 *
 */
public final class UnitLoader
{
	private HashMap<String, Byte> unitTypeID = new HashMap<String, Byte>();

	public UnitLoader(Class<? extends Unit>[] unitClasses, File unitData)
	{
		unitTypeID.put(Unit.class.getName(), Byte.MIN_VALUE);
		unitTypeID.put(Avatar.class.getName(), (byte)(Byte.MIN_VALUE+1));
	}
	public Unit createUnit(String name, boolean isGhost, byte unitType, short id)
	{
		/*
		 * temporary method filler here, this method should create the unit
		 * specified by the byte type loaded from the unit file
		 */
		Unit u = null;
		if(unitType == UnitTypeConstants.avatar)
		{
			u = new Avatar(isGhost, id);
		}
		else
		{
			u = new Unit(isGhost, id, (short)10);
		}
		return u;
	}
	public Unit createUnit
}
