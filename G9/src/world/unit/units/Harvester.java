package world.unit.units;

import world.owner.Owner;
import world.unit.Unit;
import world.unit.unitModifiers.Gatherer;

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
