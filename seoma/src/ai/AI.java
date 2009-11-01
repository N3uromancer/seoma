package ai;

import utilities.MathUtil;
import gameEngine.world.unit.Unit;

/**
 * interfaces a unit's ai with the game world
 * @author Jack
 *
 */
public abstract class AI
{
	/**
	 * the unit this ai manages
	 */
	Unit u;
	
	public AI(Unit u)
	{
		this.u = u;
	}
	public abstract void performCommands(double tdiff); //performs the ai commands
	/**
	 * moves the unit towards the passed location by the movement speed of the unit
	 * @param l the location the unit is told to move towards
	 */
	public void move(double tdiff, double[] l)
	{
		double dist = MathUtil.distance(u.getLocation()[0], u.getLocation()[1], l[0], l[1]);
		double m = tdiff*u.getMovement();
		if(dist > m)
		{
			double a = m/dist;
			double x = u.getLocation()[0] + a*(l[0]-u.getLocation()[0]);
			double y = u.getLocation()[1] + a*(l[1]-u.getLocation()[1]);
			u.setLocation(x, y);
		}
		else
		{
			u.setLocation(l[0], l[1]);
		}
	}
}
