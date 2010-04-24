package world.vehicle;

import java.awt.Color;
import java.awt.Graphics2D;

import pathfinder.Path;
import utilities.MathUtil;
import world.modifier.Pathable;
import world.terrain.CircleTerrain;

/**
 * represents an extensible pathable object
 * @author Secondary
 *
 */
public class Vehicle extends CircleTerrain implements Pathable
{
	private Path path;
	private double priority = 0;
	private double[] v = {0, 0};
	private double maxSpeed;
	private double[] f = {0, 0};
	private double maxForce;
	
	/**
	 * creates a new vehicle, starts with no velocity
	 * @param l initial location of the vehicle
	 * @param radius radius of the vehicle
	 * @param maxSeed max speed of the vehicle before it is clipped
	 * @param maxForce the max force the vehicle can experience before it is clipped
	 */
	public Vehicle(double[] l, double radius, double maxSpeed, double maxForce)
	{
		super(l, radius);
		this.maxSpeed = maxSpeed;
		this.maxForce = maxForce;
	}
	public Path getPath()
	{
		return path;
	}
	public double getPriority()
	{
		return priority;
	}
	public void setPath(Path path)
	{
		this.path = path;
	}
	public void setPriority(double priority)
	{
		this.priority = priority;
	}
	public double[] getVelocity()
	{
		return v;
	}
	public void setVelocity(double[] v)
	{
		this.v = v;
		double speed = MathUtil.magnitude(v);
		if(speed > maxSpeed)
		{
			double p = maxSpeed / speed;
			this.v[0]*=p;
			this.v[1]*=p;
		}
	}
	public boolean isMoving()
	{
		return !(v[0] == 0 && v[1] == 0);
	}
	public void addForce(double[] f)
	{
		this.f[0]+=f[0];
		this.f[1]+=f[1];
	}
	public void clearForces()
	{
		f = new double[]{0, 0};
	}
	public double[] getTotalForce()
	{
		double force = MathUtil.magnitude(f);
		if(force > maxForce)
		{
			double p = maxForce / force;
			f[0]*=p;
			f[1]*=p;
		}
		return f;
	}
	public void updateLocation(double tdiff)
	{
		l[0]+=v[0]*tdiff;
		l[1]+=v[1]*tdiff;
	}
	/**
	 * draws the velocity and force vectors for the vehicle,
	 * velocity is in red, force is in cyan
	 * @param g
	 * @param scale the scale value for the length of the vectors,
	 * scale=1 yields vectors of their actual length
	 */
	public void drawStats(Graphics2D g, double scale)
	{
		g.setColor(Color.cyan);
		g.drawLine((int)l[0], (int)l[1], (int)(l[0]+f[0]*scale), (int)(l[1]+f[1]*scale));
		g.setColor(Color.red);
		g.drawLine((int)l[0], (int)l[1], (int)(l[0]+v[0]*scale), (int)(l[1]+v[1]*scale));
	}
}
