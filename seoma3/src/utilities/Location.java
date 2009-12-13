package utilities;

import java.awt.Point;

public class Location implements Comparable<Location>
{
	public double x;
	public double y;
	
	public Location(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	/**
	 * translates the location by the passed values
	 * @param xover x translation amount
	 * @param yover y translation amount
	 * @param zover z translation amount
	 */
	public void translate(double xover, double yover)
	{
		x+=xover;
		y+=yover;
	}
	/**
	 * @return returns the Point equivalent of the location
	 */
	public Point getPoint()
	{
		return new Point((int)x, (int)y);
	}
	public String toString()
	{
		return new String("("+x+", "+y+")");
	}
	public int compareTo(Location l)
	{
		if(l.x == x && l.y == y)
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
}
