package world.action;

import java.nio.ByteBuffer;

import world.World;
import world.unit.Unit;

/**
 * used to generate a new attack by a specific unit
 * @author Jack
 *
 */
public final class AttackAction extends Action
{
	private Unit u;
	
	public AttackAction(Unit u)
	{
		this.u = u;
	}
	public void executeAction(World w, ByteBuffer actionData)
	{
		byte direction = actionData.get();
		u.getInventory().getPrimaryWeapon().executeAttack(w);
		/*
		 * calls the execute attack method of the primary weapon of the unit executing the attack action
		 */
	}
	public byte[] getInitialState()
	{
		return null;
	}
	public void loadInitialState(byte[] b)
	{
		
	}
}
