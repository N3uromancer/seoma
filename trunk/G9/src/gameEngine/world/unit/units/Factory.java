package gameEngine.world.unit.units;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Building;

/**
 * constructs basic ground units
 * @author Jack
 *
 */
public class Factory extends Building
{
	public Factory(Owner o, double x, double y)
	{
		super("factory s1", o, x, y, 50, 50, null);
		buildTree.add("tank");
		buildTree.add("builder");
	}
}
