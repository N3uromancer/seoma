package world.shot.shots;

import world.owner.Owner;
import world.shot.StaticVelocityShot;
import world.unit.Unit;

public class AntiTankRound extends StaticVelocityShot
{
	public AntiTankRound(double x, double y, Unit target, Owner o)
	{
		super(x, y, 2, 2, target, 290, 11, o);
	}
}
