package gameEngine.world.animation.animations;

import gameEngine.world.unit.Unit;

public class UnitExplosion extends Explosion
{

	public UnitExplosion(Unit u)
	{
		super(u.getLocation()[0], u.getLocation()[1], u.getWidth(), u.getHeight(), 
				u.getOwner().getColor(), 3, 4);
	}
}