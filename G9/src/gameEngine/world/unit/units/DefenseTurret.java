package gameEngine.world.unit.units;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Building;
import gameEngine.world.weapon.weapons.AntiTankTurret;

public class DefenseTurret extends Building
{
	public DefenseTurret(Owner o, double x, double y)
	{
		super("defense turret", o, x, y, 30, 30, 120, new AntiTankTurret(), 12, 70);
	}
}
