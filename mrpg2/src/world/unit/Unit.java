package world.unit;

import geom.Rectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import world.modifier.Drawable;
import world.modifier.GameObject;
import world.modifier.Movable;
import world.modifier.Updateable;

public abstract class Unit extends GameObject implements Movable, Drawable, Updateable
{
	/**
	 * the location of the unit
	 */
	double[] l;
	double r = 30;
	
	public Unit(double[] l)
	{
		this.l = l;
	}
	public Rectangle getBounds()
	{
		return new Rectangle(l[0]-r/2, l[1]-r/2, r, r);
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
		g.fillOval((int)(l[0]-r/2), (int)(l[1]-r/2), (int)r, (int)r);
	}
	public void write(DataOutputStream dos) throws IOException
	{
		dos.writeDouble(l[0]);
		dos.writeDouble(l[1]);
	}
	public void read(DataInputStream dis) throws IOException
	{
		l  = new double[2];
		l[0] = dis.readDouble();
		l[1] = dis.readDouble();
	}
}
