package ai.humanAI.userCommand;

import ai.humanAI.HumanAI;
import gameEngine.world.World;
import gameEngine.world.unit.Unit;

/**
 * moves units
 * @author Jack
 *
 */
public class MoveCommand extends UserCommand
{
	double x;
	double y;
	
	public MoveCommand(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	public void updateUnit(Unit u, HumanAI ai, boolean override, World w)
	{
		if(u.isSelected())
		{
			if(override)
			{
				u.clearActions();
			}
			ai.moveUnit(u, x, y, w);
		}
	}
}
