package world.unit;

import java.awt.Color;
import java.awt.Graphics2D;

import modifier.Drawable;
import modifier.GameObject;
import modifier.Movable;
import modifier.Updateable;

public abstract class Unit extends GameObject implements Movable, Drawable, Updateable
{
	/**
	 * the location of the unit
	 */
	double[] l;
	
	public Unit(double[] l)
	{
		this.l = l;
	}
	public double[] getLocation()
	{
		return l;
	}
	public void setLocation(double[] l)
	{
		this.l = l;
	}
	public void draw(Graphics2D g, int width, int height)
	{
		g.setColor(Color.red);
		double r = 30;
		g.fillOval((int)(l[0]-r/2), (int)(l[1]-r/2), (int)r, (int)r);
	}
}
