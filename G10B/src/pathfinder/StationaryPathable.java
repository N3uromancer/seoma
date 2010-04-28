package pathfinder;

import pathfinder.path.Path;
import world.modifier.Pathable;

/**
 * defines a stationary pathable object solely for the purposes of implementing
 * various components of the pathfinder
 * 
 * only location and priority components of the pathable interface are correctly implemented
 * @author Secondary
 *
 */
public final class StationaryPathable implements Pathable
{
	private double[] l;
	private double radius;
	private double priority;
	
	public StationaryPathable(double[] l, double radius)
	{
		this.l = l;
		this.radius = radius;
	}
	public Path getPath()
	{
		return null;
	}
	public double getRadius()
	{
		return radius;
	}
	public double getMaxSpeed()
	{
		return 0;
	}
	public double[] getTarget()
	{
		return null;
	}
	public double[] getVelocity()
	{
		return new double[]{0, 0};
	}
	public boolean isMoving()
	{
		return false;
	}
	public double[] getLocation()
	{
		return l;
	}
	public void setLocation(double[] l){}
	public double getPriority()
	{
		return priority;
	}
	public void setPriority(double priority)
	{
		this.priority = priority;
	}
	public void addForce(double[] f){}
	public void clearForces(){}
	public double[] getTotalForce()
	{
		return null;
	}
	public void setPath(Path path){}
	public void updateLocation(double tdiff){}
	public void setVelocity(double[] v){}
}
