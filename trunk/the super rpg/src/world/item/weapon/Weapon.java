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
	public abstract void executeAttack(World w);
}
