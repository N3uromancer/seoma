package world.unit;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * a unit that moves randomly
 * @author Jack
 *
 */
public final class MovingUnit extends Unit
{
	private double x;
	private double y;
	private double width;
	private double height;
	
	/**
	 * creates a new unit that moves randomly when updated
	 * @param l
	 * @param x the x coordinate of the  bottom left corner of the region
	 * in which the unit is randomly moved
	 * @param y the y coordinate of the  bottom left corner of the region
	 * in which the unit is randomly moved
	 * @param width the width of the region the unit is to move within
	 * @param height the height of the region the unit is to move within
	 */
	public MovingUnit(double[] l, double x, double y, double width, double height)
	{
		super(l);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public void update(double tdiff)
	{
		double movement = 10;
		l[0]+=Math.random()*movement-movement/2;
		if(l[0] < x)
		{
			l[0] = x;
		}
		else if(l[0] > x+width)
		{
			l[0] = x+width;
		}
		l[1]+=Math.random()*movement-movement/2;
		if(l[1] < y)
		{
			l[1] = y;
		}
		else if(l[1] > y+height)
		{
			l[1] = y+height;
		}
	}
	public void writeState(DataOutputStream dos)
	{
		super.writeState(dos);
		try
		{
			dos.writeInt((int)x);
			dos.writeInt((int)y);
			dos.writeInt((int)width);
			dos.writeInt((int)height);
		}
		catch(IOException e){}
	}
	public void readState(ByteBuffer buff)
	{
		super.readState(buff);
		x = buff.getInt();
		y = buff.getInt();
		width = buff.getInt();
		height = buff.getInt();
	}
}
