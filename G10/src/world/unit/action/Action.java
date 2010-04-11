package world.unit.action;

import world.World;

/**
 * an act a unit can carry out
 * @author Secondary
 *
 */
public interface Action
{
	/**
	 * performs the action
	 * @param w
	 * @param tdiff
	 * @return returns true if the action has been completed, false otherwise
	 */
	public boolean performAction(World w, double tdiff);
	/**
	 * called when the action is first begun
	 * @param w
	 */
	public void initiateAction(World w);
	/**
	 * called if the action is canceled
	 */
	public void cancelAction();
}
