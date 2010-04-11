package geom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * defines a rectangular region of space
 * @author Jack
 *
 */
public class Rectangle implements Boundable
{
	protected double x;
	protected double y;
	protected double width;
	protected double height;
	
	/**
	 * defines a region
	 * @param x the x position of the bottom left corner of the region
	 * @param y the y position of the bottom left corner of the region
	 * @param width the width of the region
	 * @param height the height of the region
	 */
	public Rectangle(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public Rectangle(Rectangle r)
	{
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
	}
	public double[] getCenter()
	{
		return new double[]{x+width/2, y+height/2};
	}
	public Rectangle getBounds()
	{
		return new Rectangle(x, y, width, height);
	}
	public Rectangle(DataInputStream dis)
	{
		try
		{
			readRegion(dis);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public boolean contains(double px, double py)
	{
		return px >= x && px <= x+width && py >= y && py <= y+height;
	}
	public boolean intersects(Rectangle r)
	{
		if(r.getLocation()[0]+r.getWidth() < x || r.getLocation()[0] > x+width)
		{
			return false;
		}
		else if(r.getLocation()[1]+r.getHeight() < y || r.getLocation()[1] > y+height)
		{
			return false;
		}
		return true;
	}
	public double[] getLocation()
	{
		double[] d = {x, y};
		return d;
	}
	public void setLocation(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	public double getWidth()
	{
		return width;
	}
	public double getHeight()
	{
		return height;
	}
	/**
	 * writes to the passed output stream the variables of this rectangle
	 * @param dos
	 * @throws IOException
	 */
	public void writeRegion(DataOutputStream dos) throws IOException
	{
		dos.writeDouble(x);
		dos.writeDouble(y);
		dos.writeDouble(width);
		dos.writeDouble(height);
	}
	/**
	 * reads the passed data stream and sets the variables for this rectangle
	 * @param dis
	 * @throws IOException
	 */
	public void readRegion(DataInputStream dis) throws IOException
	{
		x = dis.readDouble();
		y = dis.readDouble();
		width = dis.readDouble();
		height = dis.readDouble();
	}
	public String toString()
	{
		return "x="+x+", y="+y+", w="+width+", h="+height+", area="+(width*height);
	}
}
