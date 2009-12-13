package gameEngine.world.shot.shots;

import gameEngine.world.owner.Owner;
import gameEngine.world.shot.StaticVelocityShot;
import gameEngine.world.unit.Unit;

public class LightRound extends StaticVelocityShot
{
	public LightRound(double x, double y, Unit target, Owner o)
	{
		super(x, y, 2, 2, target, 170, 10, o);
	}
}
