package world.unit.action;

import world.World;
import world.unit.Unit;
import world.unit.attribute.Attribute;

/**
 * defines an action that moves a unit in the specified direction
 * according to the movement speed of the unit
 * @author Jack
 *
 */
public final class MoveTowards implements Action
{
	private double[] t;
	
	/**
	 * creates a new movement action to move a unit in a specified direction
	 * @param t the direction the unit should move in
	 */
	public MoveTowards(double[] t)
	{
		this.t = t;
	}
	public boolean performAction(Unit u, World w, double tdiff)
	{
		double[] l = u.getLocation();
		double m = u.getAttributeManager().getAttribute(Attribute.movement);
		u.setLocation(new double[]{l[0]+t[0]*m*tdiff, l[1]+t[1]*m*tdiff});
		return true;
	}
}
