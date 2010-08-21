package world.unit.action;

import world.World;
import world.unit.Unit;

/**
 * defines a class for making a unit perform an operation
 * @author Jack
 *
 */
public interface Action
{
	/**
	 * performs the action
	 * @param u the unit the action is being performed for
	 * @param w
	 * @param tdiff
	 * @return returns true if the action is complete, false otherwise
	 */
	public boolean performAction(Unit u, World w, double tdiff);
}
