package gameEngine.world.action;

import javax.media.opengl.GL;

/**
 * the action of every unit
 * @author Jack
 *
 */
public abstract class Action
{
	String name;
	public Action(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	/**
	 * performs the actions
	 * @param tdiff the time passed since the last time the action
	 * was performed
	 * @return returns true if the action is completed
	 */
	public abstract boolean performAction(double tdiff);
	/**
	 * cancels the action, called whenever the action is changed from
	 * one thing to another
	 */
	public abstract void cancelAction();
	public String toString()
	{
		return name;
	}
	public abstract void drawAction(GL gl);
	/**
	 * called when the action is initially begun, for intsance, if there
	 * were several actions queued in a units action queue this action may
	 * not run immediately when it was assigned
	 */
	public abstract void startAction();
}
