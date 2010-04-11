package world.unit;

import geom.Circle;

import java.awt.Graphics2D;

import world.World;
import world.modifier.Destructable;
import world.modifier.Drawable;

public class Unit extends Circle implements Drawable, Destructable
{
	double maxLife;
	double life;
	boolean isDead = false;
	
	/**
	 * creates a default unit for test purposes
	 * @param l
	 */
	public Unit(double[] l)
	{
		this(l, 7, 100);
	}
	public Unit(double[] l, double radius, double maxLife)
	{
		super(l, radius);
		this.maxLife = maxLife;
		life = maxLife;
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
		
	}
}
