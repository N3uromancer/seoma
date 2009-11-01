package gameEngine.world.unit;

import ai.AI;
import utilities.Region;

public class Unit extends Region
{
	double movement;
	AI ai; //the ai managing this unit
	
	public Unit(double x, double y, double width, double height, double movement)
	{
		super(x, y, width, height);
		this.movement = movement;
	}
	/**
	 * returns how far this unit can move
	 * @return returns how far this unit can move
	 */
	public double getMovement()
	{
		return movement;
	}
	/**
	 * returns the ai managing this unit
	 * @return
	 */
	public AI getAI()
	{
		return ai;
	}
}
