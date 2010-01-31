package world.shot.shots;

import world.owner.Owner;
import world.shot.StaticVelocityShot;
import world.unit.Unit;

public class LightRound extends StaticVelocityShot
{
	public LightRound(double x, double y, Unit target, Owner o)
	{
		super(x, y, 2, 2, target, 170, 3, o);
	}
}
