package ai.humanAI.userCommand;

import ai.AI;
import gameEngine.world.World;
import gameEngine.world.unit.Unit;

public class DeselectCommand extends UserCommand
{
	public void updateUnit(Unit u, AI ai, boolean override, World w)
	{
		u.setSelected(false);
	}
}
