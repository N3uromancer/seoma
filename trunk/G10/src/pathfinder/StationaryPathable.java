package pathfinder;

import java.util.HashMap;

import pathfinder.graph.Node;
import world.modifier.Pathable;

/**
 * defines a stationary pathable object solely for the purposes of implementing
 * various components of the pathfinder
 * 
 * the priority features
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
	public HashMap<Integer, Node> getPath()
	{
		return null;
	}
	public int getPathNodeIndex()
	{
		return 0;
	}
	public double getRadius()
	{
		return radius;
	}
	public void setPath(HashMap<Integer, Node> path){}
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
	public void setPathNodeIndex(int index){}
}
