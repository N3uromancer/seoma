package world.animation.animations;

import world.unit.Unit;

public class UnitExplosion extends Explosion
{

	public UnitExplosion(Unit u)
	{
		super(u.getLocation()[0], u.getLocation()[1], u.getBounds().getWidth(), u.getBounds().getHeight(), 
				u.getOwner().getColor(), 3, 4);
	}
}