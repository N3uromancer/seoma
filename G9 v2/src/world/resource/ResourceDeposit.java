package world.resource;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.media.opengl.GL;

import world.World;


/**
 * represents a game resource, resources do not affect pathfinding
 * @author Jack
 *
 */
public class ResourceDeposit
{
	double[] location;
	double total;
	double max;
	double increase;
	
	//display variables
	double r = Math.random()*360; //the ange the deposit is rotated
	double ra = 10;
	double radius; //the radius of the deposit
	int sides; //the number of sides the deposit is drawn with
	
	double expansion = 0; //expands and contracts the deposit for visual effects
	double expansionScale = .08;
	
	public ResourceDeposit(double[] location, double total, double increase, double radius, int sides)
	{
		this.location = location;
		this.total = total;
		this.increase = increase;
		max = total;
		this.radius = radius;
		this.sides = sides;
	}
	/**
	 * returns the radius of the resource deposit
	 * @return
	 */
	public double getRadius()
	{
		return radius;
	}
	public double[] getLocation()
	{
		return location;
	}
	/**
	 * gets the total amount of resources remaining at this resource deposit
	 * @return
	 */
	public double getTotalResources()
	{
		return total;
	}
	/**
	 * sets the total resources remaining at this resource deposit
	 * @param setter
	 */
	public void setTotalResources(double setter)
	{
		total = setter;
	}
	public ResourceDeposit(DataInputStream dis) throws IOException
	{
		location = new double[2];
		for(int i = 0; i < 2; i++)
		{
			location[i] = dis.readDouble();
		}
		max = dis.readDouble();
		total = max;
		increase = dis.readDouble();
		radius = dis.readDouble();
		sides = dis.readInt();
	}
	public void writeResourceDeposit(DataOutputStream dos) throws IOException
	{
		String name = getClass().getName();
		dos.writeInt(name.length());
		dos.writeChars(name);
		for(int i = 0; i < location.length; i++)
		{
			dos.writeDouble(location[i]);
		}
		
		/*for(int i = 0; i < location.length; i++)
		{
			dos.writeDouble(location[i]);
		}
		dos.writeDouble(total);
		dos.writeDouble(increase);
		dos.writeDouble(radius);
		dos.writeInt(sides);*/
	}
	public void draw(GL gl, double depth)
	{
		gl.glPushMatrix();
		gl.glColor4d(.7, .2, .9, .4);
		gl.glTranslated(location[0], location[1], depth);
		gl.glScaled(1+Math.sin(expansion)*expansionScale, 1+Math.sin(expansion)*expansionScale, 0);
		gl.glRotated(r, 0, 0, 1);
		gl.glLineWidth(3);
		gl.glBegin(GL.GL_LINE_LOOP);
		int increment = 360/sides;
		for(int i = 0; i < 360; i+=increment)
		{
			double x = Math.cos(i)+Math.sin(i);
			double y = Math.cos(i)-Math.sin(i);
			gl.glVertex2d(x*radius, y*radius);
		}
		gl.glEnd();
		//r+=2;
		//expansion+=.1;
		gl.glPopMatrix();
	}
	public void update(World w, double tdiff)
	{
		total+=increase*tdiff;
		//System.out.println("total = "+total+", increase = "+increase);
		//System.out.println(max);
		r+=ra*tdiff*total/max;
		//expansion+=3*tdiff;
		if(total > max)
		{
			total = max;
		}
	}
}
