package gameEngine.world.unit;

import ai.AI;
import utilities.region.RectRegion;

public class Unit extends RectRegion
{
	double movement;
	public AI ai; //the ai managing this unit
	int life, maxLife;
	String name;
	
	public Unit(double x, double y, double width, double height, double movement, int maxLife, String name)
	{
		super(x, y, width, height);
		this.movement = movement;
		this.name = name;
		this.maxLife = maxLife;
		life = maxLife;
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
