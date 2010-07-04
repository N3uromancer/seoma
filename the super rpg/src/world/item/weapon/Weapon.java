package world.item.weapon;

import world.World;
import world.item.Item;

/**
 * defines the outline for a weapon, weapons execute attacks which damage units
 * @author Jack
 *
 */
public abstract class Weapon extends Item
{
	/**
	 * executes the attack
	 * @param w
	 * @param data the data the attack needs in order to execute properly
	 */
	public abstract void executeAttack(World w, byte[] data);
}
