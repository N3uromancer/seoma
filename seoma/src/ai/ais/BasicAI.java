package ai.ais;

import ai.AI;
import gameEngine.world.unit.Unit;

public class BasicAI extends AI
{
	Unit u;
	public BasicAI(Unit u)
	{
		super(u);
		this.u = u;
	}
	
	public void performCommands(double tdiff)
	{
		double[] p = u.getLocation();
		p[0] += 100; //simple ai that always moves right
		move(tdiff, p);
	}
}
