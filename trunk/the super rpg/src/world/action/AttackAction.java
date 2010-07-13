package world.action;

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
	
	public AttackAction(byte actionID, Unit u)
	{
		super(actionID);
		this.u = u;
	}
	public void executeAction(byte[] pertData, World w)
	{
		byte direction = pertData[0];
		u.getInventory().getPrimaryWeapon().executeAttack(w, direction);
	}
}
