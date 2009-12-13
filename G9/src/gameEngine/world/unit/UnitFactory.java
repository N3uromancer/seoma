package gameEngine.world.unit;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.units.*;

public final class UnitFactory
{
	public static Unit createUnit(String name, Owner o, double x, double y)
	{
		if(name.equalsIgnoreCase("tank"))
		{
			return new Tank(o, x, y);
		}
		else if(name.equalsIgnoreCase("builder s1"))
		{
			return new Builder(o, x, y);
		}
		else if(name.equalsIgnoreCase("factory s1"))
		{
			return new Factory(o, x, y);
		}
		return null;
	}
}
