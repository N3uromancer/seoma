package world.unit.script;

import world.World;
import world.unit.Unit;

/**
 * defines a script that controls a unit, the purpose of scripts is to queue actions
 * for a given unit to execute
 * @author Jack
 *
 */
public abstract class Script
{
	/**
	 * updates the script and queues necesary actions for the unit to perform
	 * @param u the unit that is running the script
	 * @param w
	 * @param tdiff
	 */
	public abstract void updateScript(Unit u, World w, double tdiff);
}
