package gameEngine.world.unit.unitModifiers;

import gameEngine.world.unit.Unit;

/**
 * specifies that a unit is capable of building other units
 * @author Jack
 *
 */
public interface Builder
{
	public boolean canBuild(Class<? extends Unit> c);
}
