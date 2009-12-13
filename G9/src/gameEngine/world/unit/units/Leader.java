package gameEngine.world.unit.units;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.unitModifiers.Builder;

/**
 * a basic builder
 * 
 * draw with a picture of a hammer
 * @author Jack
 *
 */
public class Leader extends Unit implements Builder
{

	public Leader(Owner o, double x, double y)
	{
		super("leader", o, x, y, 15, 11, 100, null);
	}
	public boolean canBuild(Class<? extends Unit> c)
	{
		return c == Factory.class;
	}
}
