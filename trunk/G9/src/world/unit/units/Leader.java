package world.unit.units;

import world.owner.Owner;
import world.unit.Unit;
import world.unit.unitModifiers.Builder;

/**
 * a basic builder
 * @author Jack
 *
 */
public class Leader extends Unit implements Builder
{
	public Leader(Owner o, double x, double y)
	{
		super("leader", o, x, y, 30, 20, 200, 60, null, 0, 0);
	}
	public boolean canBuild(Class<? extends Unit> c)
	{
		return c == Factory.class || c == Refinery.class || c == DefenseTurret.class;
	}
}
