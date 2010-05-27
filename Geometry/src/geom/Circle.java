package geom;

import java.awt.Graphics2D;

public class Circle
{
	public double[] l;
	public double radius;
	
	/**
	 * creates a new circle
	 * @param l the location of the center of the circle
	 * @param radius
	 */
	public Circle(double[] l, double radius)
	{
		this.l = l;
		this.radius = radius;
	}
	/**
	 * tests for intersection
	 * @param c
	 * @return returns true if this circle intersects the passed circle
	 */
	public boolean intersects(Circle c)
	{
		return getDistance(c) < c.radius+radius;
	}
	/**
	 * tests to see if the passed point is contained within the circle
	 * @param p
	 * @return returns true if the passed point is within the circle, false otherwise
	 */
	public boolean contains(double[] p)
	{
		return Math.sqrt(Math.pow(p[0]-l[0], 2)+Math.pow(p[1]-l[1], 2)) < radius;
	}
	public double getDistance(Circle c)
	{
		double x = c.l[0]-l[0];
		double y = c.l[1]-l[1];
		return Math.sqrt(x*x+y*y);
	}
	public void drawCircle(Graphics2D g)
	{
		int x = (int)(l[0]-radius);
		int y = (int)(l[1]-radius);
		g.fillOval(x, y, (int)radius*2, (int)radius*2);
	}
	public Rectangle getBounds()
	{
		return new Rectangle(l[0]-radius, l[1]-radius, radius*2, radius*2);
	}
	public void setLocation(double[] l)
	{
		this.l = l;
	}
	public double[] getLocation()
	{
		return l;
	}
	public double getRadius()
	{
		return radius;
	}
	public String toString()
	{
		return "l=("+l[0]+", "+l[1]+"), r="+radius;
	}
}
