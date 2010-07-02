package world.unit.action;

import world.World;

/**
 * used to generate a new attack by a specific unit
 * @author Jack
 *
 */
public final class AttackAction extends Action
{
	public void executeAction(World w)
	{
		/*
		 * calls the execute attack method of the primary weapon of the unit executing the attack action
		 */
	}
	public byte[] getInitialState()
	{
		return null;
	}
	public void loadInitialState(byte[] b, World w)
	{
		
	}
}
