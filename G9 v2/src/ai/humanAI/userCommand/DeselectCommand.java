package ai.humanAI.userCommand;

import ai.humanAI.HumanAI;
import gameEngine.world.World;
import gameEngine.world.unit.Unit;

public class DeselectCommand extends UserCommand
{
	public void updateUnit(Unit u, HumanAI ai, boolean override, World w)
	{
		u.setSelected(false);
		ai.setSelectedUnits(ai.getSelectedUnits()-1);
	}
}
