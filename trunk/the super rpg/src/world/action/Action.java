package world.action;

import world.World;

/**
 * defines a game action, everything that units do (other than move) is an action,
 * actions are instantaneous but can spawn other updateable objects for the world
 * to process
 * @author Jack
 *
 */
public abstract class Action
{
	private byte actionID;
	
	public Action(byte actionID)
	{
		this.actionID = actionID;
	}
	/**
	 * gets the id of the action, used to differentiate between various actions registered
	 * to a given network updateable
	 * @return returns the id of the action
	 */
	public byte getActionID()
	{
		return actionID;
	}
	/**
	 * executes an instantaneous action, ex. attack, spell, open chest, etc
	 * @param pertData the data the method needs to actually complete the action,
	 * ex. a melee attack would require 1 byte specifying the direction, the information
	 * needed to complete the action is expected to reside in the action data byte buffer
	 * @param w
	 */
	public abstract void executeAction(byte[] pertData, World w);
}
