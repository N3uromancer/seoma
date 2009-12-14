package gameEngine.world.unit.units;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Building;
import gameEngine.world.unit.unitModifiers.Refiner;

public class Refinery extends Building implements Refiner
{
	public Refinery(Owner o, double x, double y)
	{
		super("refinery", o, x, y, 70, 70, 100, null, 20, 50);
	}
}
