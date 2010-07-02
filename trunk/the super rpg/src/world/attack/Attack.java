package world.attack;

import world.World;
import world.item.weapon.Weapon;
import world.unit.Unit;

/**
 * defines an attack in the game, attacks
 * @author Jack
 *
 */
public abstract class Attack
{
	private Unit u;
	private Weapon w;
	private byte direction;
	
	/**
	 * creates a new attack
	 * @param u
	 * @param w
	 * @param direction
	 * @param simulate if true then the attack is simulated and no damage
	 * is dealt, otherwise units are damaged as normal
	 */
	public Attack(Unit u, Weapon w, byte direction, boolean simulate)
	{
		this.u = u;
		this.w = w;
		this.direction = direction;
	}
	public void update(World w, double tdiff)
	{
		//swing sword here
		//damage hit units here
	}
}