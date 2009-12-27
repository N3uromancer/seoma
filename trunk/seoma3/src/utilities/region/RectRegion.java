package utilities.region;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.media.opengl.GL;

/**
 * defines a rectangular region of space
 * @author Jack
 *
 */
public class RectRegion implements Region
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
	public RectRegion(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public RectRegion(Region r)
	{
		x = r.getLocation()[0];
		y = r.getLocation()[1];
		width = r.getWidth();
		height = r.getHeight();
	}
	public RectRegion(DataInputStream dis)
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
	public boolean intersects(Region r)
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
	public void drawRegion(GL gl, double depth)
	{
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3d(x, y, depth);
		gl.glVertex3d(x+width, y, depth);
		gl.glVertex3d(x+width, y+height, depth);
		gl.glVertex3d(x, y+height, depth);
		gl.glEnd();
	}
	public void fillRegion(GL gl, double depth)
	{
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3d(x, y, depth);
		gl.glVertex3d(x+width, y, depth);
		gl.glVertex3d(x+width, y+height, depth);
		gl.glVertex3d(x, y+height, depth);
		gl.glEnd();
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
	public void writeRegion(DataOutputStream dos) throws IOException
	{
		dos.writeDouble(x);
		dos.writeDouble(y);
		dos.writeDouble(width);
		dos.writeDouble(height);
	}
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
