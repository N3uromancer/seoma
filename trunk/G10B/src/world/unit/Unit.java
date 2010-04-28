package world.unit;

import java.awt.Color;
import java.awt.Graphics2D;

import world.World;
import world.modifier.Destructable;
import world.unit.action.Action;
import world.vehicle.Vehicle;

public class Unit extends Vehicle implements Destructable
{
	double maxLife;
	double life;
	boolean isDead = false;
	
	Action a;
	boolean actionInitiated = false;
	
	/**
	 * creates a default unit for test purposes
	 * @param l
	 */
	public Unit(double[] l)
	{
		this(l, 7, 100, 150, 10);
	}
	public Unit(double[] l, double radius, double maxLife, double maxSpeed, double maxForce)
	{
		super(l, radius, maxSpeed, maxForce);
		this.maxLife = maxLife;
		life = maxLife;
	}
	public void setAction(Action a)
	{
		this.a = a;
	}
	public void draw(Graphics2D g)
	{
		g.setColor(Color.blue);
		int x = (int)(l[0]-radius);
		int y = (int)(l[1]-radius);
		g.fillOval(x, y, (int)radius*2, (int)radius*2);
		drawStats(g, .5);
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
	public void update(World w, double tdiff)
	{
		if(life < 0)
		{
			setDead();
		}
		if(a != null)
		{
			//System.out.println("performing action...");
			if(!actionInitiated)
			{
				a.initiateAction(w);
				actionInitiated = true;
			}
			boolean complete = a.performAction(w, tdiff);
			if(complete)
			{
				a = null;
				System.out.println("action removed");
			}
		}
	}
}
