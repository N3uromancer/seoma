package pathfinder;

import java.util.HashMap;

import pathfinder.graph.Node;
import world.modifier.Pathable;

/**
 * defines a stationary pathable object solely for the purposes of implementing
 * various components of the pathfinder
 * @author Secondary
 *
 */
public final class StationaryPathable implements Pathable
{
	private double[] l;
	private double radius;
	
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
}
