package gameEngine.world.unit.units;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Unit;

/**
 * a basic builder
 * 
 * draw with a picture of a hammer
 * @author Jack
 *
 */
public class Builder extends Unit
{

	public Builder(Owner o, double x, double y)
	{
		super("builder", o, x, y, 15, 11, 100, null);
		buildTree.add("factory");
	}
	
}
