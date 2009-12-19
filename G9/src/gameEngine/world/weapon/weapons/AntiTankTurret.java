package gameEngine.world.weapon.weapons;

import gameEngine.world.owner.Owner;
import gameEngine.world.shot.Shot;
import gameEngine.world.shot.shots.AntiTankRound;
import gameEngine.world.unit.Unit;
import gameEngine.world.weapon.Weapon;

public final class AntiTankTurret extends Weapon
{
	public AntiTankTurret()
	{
		super(270);
	}
	protected Shot getShot(double x, double y, Unit target, Owner o)
	{
		return new AntiTankRound(x, y, target, o);
	}
}
