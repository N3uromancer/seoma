package world.unit.script;

import world.World;
import world.unit.Unit;
import world.unit.action.MoveTowards;

public class WanderScript extends Script
{
	double[] dir; //the direction the unit is wandering
	double time = 2; //time until a new direction is picked
	double elapsed = 0;
	
	public void updateScript(Unit u, World w, double tdiff)
	{
		if(elapsed >= time || dir == null)
		{
			dir = new double[]{Math.random(), Math.random()};
			elapsed = 0;
		}
		elapsed+=tdiff;
		u.queueAction(new MoveTowards(dir));
	}
}
