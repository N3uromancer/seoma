package world.unit.units;

import world.owner.Owner;
import world.unit.Building;
import world.unit.Unit;
import world.unit.unitModifiers.Builder;

/**
 * constructs basic ground units
 * @author Jack
 *
 */
public class Factory extends Building implements Builder
{
	public Factory(Owner o, double x, double y)
	{
		super("factory", o, x, y, 50, 50, 150, null, 7, 100);
	}
	public boolean canBuild(Class<? extends Unit> c)
	{
		return (c == Harvester.class || c == Tank.class || c == Engineer.class);
	}
}
