package gameEngine.world.unit.units;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Unit;
import gameEngine.world.weapon.weapons.MachineGun;

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
		super("tank", o, x, y, 20, 15, 70, new MachineGun(), 10);
	}
	
}
