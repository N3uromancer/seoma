package world.unit.units;

import world.owner.Owner;
import world.unit.Building;
import world.weapon.weapons.AntiTankTurret;

public class DefenseTurret extends Building
{
	public DefenseTurret(Owner o, double x, double y)
	{
		super("defense turret", o, x, y, 30, 30, 120, new AntiTankTurret(), 12, 70);
	}
}
