package utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.media.opengl.GL;

/**
 * defines a region of space
 * @author Jack
 *
 */
public class Region
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
	public Region(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	/**
	 * determines if the passed location is contained inside this region
	 * @param px the x coordinate of the location
	 * @param py the y coordinate of the location
	 * @return returns true if the passed location is contained inside this
	 * region, false otherwise
	 */
	public boolean contains(double px, double py)
	{
		return px >= x && px <= x+width && py >= y && py <= y+height;
	}
	/**
	 * checks to see if the passed region intersects or is cotained within
	 * this region, if the passed region is on the edge of this region it
	 * counts as intersecting
	 * @param r
	 * @return returns true if the passed region intersects or is contained
	 * within this region, false otherwise
	 */
	public boolean intersects(Region r)
	{
		if(r.x+r.width < x || r.x > x+width)
		{
			return false;
		}
		else if(r.y+r.height < y || r.y > y+height)
		{
			return false;
		}
		return true;
	}
	public void drawRegion(GL gl)
	{
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x+width, y);
		gl.glVertex2d(x+width, y+height);
		gl.glVertex2d(x, y+height);
		gl.glEnd();
	}
	public void fillRegion(GL gl)
	{
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x+width, y);
		gl.glVertex2d(x+width, y+height);
		gl.glVertex2d(x, y+height);
		gl.glEnd();
	}
	public String toString()
	{
		return "x="+x+", y="+y+", w="+width+", h="+height+", area="+(width*height);
	}
	/**
	 * gets the location of the bottom left corner of the region
	 * @return returns a double[] of length 2 representing the x and y coordinates
	 * of the bottom left corner of the region
	 */
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
	 * writes the state of the passed region to the passed output stream
	 * @param dos
	 * @throws IOException
	 */
	public static void writeRegion(Region r, DataOutputStream dos) throws IOException
	{
		dos.writeDouble(r.x);
		dos.writeDouble(r.y);
		dos.writeDouble(r.width);
		dos.writeDouble(r.height);
	}
	/**
	 * reads and returns the state of a region from the passed input stream
	 * @param dis
	 * @return returns a region whose state was read from the passed input stream
	 * @throws IOException
	 */
	public static Region readRegion(DataInputStream dis) throws IOException
	{
		double x = dis.readDouble();
		double y = dis.readDouble();
		double width = dis.readDouble();
		double height = dis.readDouble();
		return new Region(x, y, width, height);
	}
}
