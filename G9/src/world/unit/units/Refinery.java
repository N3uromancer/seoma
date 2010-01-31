package world.unit.units;

import world.owner.Owner;
import world.unit.Building;
import world.unit.unitModifiers.Refiner;

public class Refinery extends Building implements Refiner
{
	public Refinery(Owner o, double x, double y)
	{
		super("refinery", o, x, y, 70, 70, 100, null, 10, 50);
	}
}
