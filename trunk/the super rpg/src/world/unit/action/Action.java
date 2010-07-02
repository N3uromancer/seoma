package world.unit.action;

import world.World;
import world.modifier.Initializable;

/**
 * defines a game action, everything that units do (other than move) is an action,
 * actions are instantaneous but can spawn other updateable objects for the world
 * to process
 * @author Jack
 *
 */
public abstract class Action implements Initializable
{
	/**
	 * executes an instantaneous action, ex. attack, spell, open chest, etc
	 * @param w
	 */
	public abstract void executeAction(World w);
}
