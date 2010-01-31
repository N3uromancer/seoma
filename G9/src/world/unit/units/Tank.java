package world.unit.units;

import world.owner.Owner;
import world.unit.Unit;
import world.weapon.weapons.MachineGun;

/**
 * a basic unit, weak weapon, lots of life
 * 
 * draw with a picture of a shield
 * @author Jack
 *
 */
public class Tank extends Unit
{
	public Tank(Owner o, double x, double y)
	{
		super("tank", o, x, y, 20, 15, 40, 45, new MachineGun(), 7, 20);
	}
	
}
