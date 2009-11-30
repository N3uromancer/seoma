package gameEngine.world.unit.units;

import ai.ais.*;
import gameEngine.world.unit.Unit;

public class DotaBird extends Unit
{
	public DotaBird(double x, double y, double width, double height)
	{
		super(x, y, width, height, 
			  3, //move speed
			  5, //max life
			  "DotaBird"); //name
		ai = new BasicAI(this);
	}
}
