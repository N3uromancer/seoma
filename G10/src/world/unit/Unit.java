package world.unit;

import geom.Circle;

import java.awt.Graphics2D;
import java.util.HashMap;

import pathfinder.graph.Node;

import world.World;
import world.modifier.Destructable;
import world.modifier.Drawable;
import world.modifier.Pathable;

public class Unit extends Circle implements Drawable, Destructable, Pathable
{
	double maxLife;
	double life;
	double maxSpeed;
	
	boolean isDead = false;
	
	//pathing variables
	HashMap<Integer, Node> path;
	int pathNodeIndex = 0;
	double priority;
	double[] target;
	double[] velocity;
	
	/**
	 * creates a default unit for test purposes
	 * @param l
	 */
	public Unit(double[] l)
	{
		this(l, 7, 100, 170);
	}
	public Unit(double[] l, double radius, double maxLife, double maxSpeed)
	{
		super(l, radius);
		this.maxLife = maxLife;
		life = maxLife;
		this.maxSpeed = maxSpeed;
	}
	public void draw(Graphics2D g)
	{
		int x = (int)(l[0]-radius);
		int y = (int)(l[1]-radius);
		g.fillOval(x, y, (int)radius*2, (int)radius*2);
	}
	public void destroy(World w)
	{
		
	}
	public double getLife()
	{
		return maxLife;
	}
	public void setLife(double setter)
	{
		life = setter;
		if(life <= 0)
		{
			setDead();
		}
	}
	public boolean isDead()
	{
		return isDead;
	}
	public void setDead()
	{
		isDead = true;
	}
	public void update(double tdiff)
	{
		if(life < 0)
		{
			setDead();
		}
	}
	public HashMap<Integer, Node> getPath()
	{
		return path;
	}
	public int getPathNodeIndex()
	{
		return pathNodeIndex;
	}
	public double getPriority()
	{
		return priority;
	}
	public void setPath(HashMap<Integer, Node> path)
	{
		this.path = path;
	}
	public void setPathNodeIndex(int index)
	{
		pathNodeIndex = index;
	}
	public void setPriority(double priority)
	{
		this.priority = priority;
	}
	public double getMaxSpeed()
	{
		return maxSpeed;
	}
	public double[] getTarget()
	{
		return target;
	}
	public double[] getVelocity()
	{
		return velocity;
	}
	public boolean isMoving()
	{
		return target!=null;
	}
}
