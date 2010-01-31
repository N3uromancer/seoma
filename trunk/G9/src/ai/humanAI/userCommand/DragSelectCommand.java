package ai.humanAI.userCommand;

import utilities.region.RectRegion;
import utilities.region.Region;
import ai.humanAI.HumanAI;
import gameEngine.world.World;
import gameEngine.world.unit.Unit;

public class DragSelectCommand extends UserCommand
{
	Region r;
	/**
	 * creates a new drag select command to select units based off a mouse drag
	 * @param sx the starting x position of the mouse
	 * @param sy
	 * @param ex the ending x position of the mouse
	 * @param ey
	 */
	public DragSelectCommand(double sx, double sy, double ex, double ey)
	{
		double x = ex;
		if(sx < ex)
		{
			x = sx;
		}
		double y = ey;
		if(sy < ey)
		{
			y = sy;
		}
		double width = Math.abs(ex-sx);
		double height = Math.abs(ey-ex);
		r = new RectRegion(x, y, width, height);
	}
	public void updateUnit(Unit u, HumanAI ai, boolean override, World w)
	{
		if(r.intersects(u))
		{
			u.setSelected(true);
			ai.setSelectedUnits(ai.getSelectedUnits()+1);
		}
	}
}
