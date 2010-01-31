package world.weapon.weapons;

import world.owner.Owner;
import world.shot.Shot;
import world.shot.shots.AntiTankRound;
import world.unit.Unit;
import world.weapon.Weapon;
import world.weapon.weaponModifiers.Aimable;

public final class AntiTankTurret extends Weapon implements Aimable
{
	public AntiTankTurret()
	{
		super(200);
	}
	protected Shot getShot(double x, double y, Unit target, Owner o)
	{
		return new AntiTankRound(x, y, target, o);
	}
}
