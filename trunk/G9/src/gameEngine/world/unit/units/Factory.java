package gameEngine.world.unit.units;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Building;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.unitModifiers.Builder;

/**
 * constructs basic ground units
 * @author Jack
 *
 */
public class Factory extends Building implements Builder
{
	public Factory(Owner o, double x, double y)
	{
		super("factory", o, x, y, 50, 50, 150, null, 7, 50);
	}
	public boolean canBuild(Class<? extends Unit> c)
	{
		return (c == Harvester.class || c == Tank.class);
	}
}
