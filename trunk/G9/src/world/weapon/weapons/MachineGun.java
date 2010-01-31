package world.weapon.weapons;

import world.owner.Owner;
import world.shot.Shot;
import world.shot.shots.LightRound;
import world.unit.Unit;
import world.weapon.Weapon;
import world.weapon.weaponModifiers.Aimable;

public final class MachineGun extends Weapon implements Aimable
{
	public MachineGun()
	{
		super(100);
	}
	protected Shot getShot(double x, double y, Unit target, Owner o)
	{
		return new LightRound(x, y, target, o);
	}
}
