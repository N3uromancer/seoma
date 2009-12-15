package gameEngine.world.unit.units;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.unitModifiers.Gatherer;

/**
 * basic unit good for harvesting resources
 * @author Jack
 *
 */
public class Harvester extends Unit implements Gatherer
{
	double load = 0;
	
	public Harvester(Owner o, double x, double y)
	{
		super("harvester", o, x, y, 15, 11, 10, 90, null, 3, 10);
	}
	public double getLoad()
	{
		return load;
	}
	public double getMaxLoad()
	{
		return 20;
	}
	public void setLoad(double setter)
	{
		load = setter;
	}
	public double getGatherRate()
	{
		return 4;
	}
}
