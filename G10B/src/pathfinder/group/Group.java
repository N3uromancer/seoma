package pathfinder.group;

import java.util.HashSet;

import pathfinder.path.Path;
import world.modifier.Pathable;

/**
 * defines a group of pathable objects
 * @author Secondary
 *
 */
public class Group
{
	private HashSet<Pathable> p;
	private double[] center;
	private double maxRadius;
	
	public Group(HashSet<Pathable> p, double[] center, double maxRadius)
	{
		this.p = p;
		this.center = center;
		this.maxRadius = maxRadius;
	}
	/**
	 * returns the location of the center of the group
	 * @return
	 */
	public double[] getCenter()
	{
		return center;
	}
	/**
	 * returns the elements in the group
	 * @return returns the elements in the group
	 */
	public HashSet<Pathable> getElements()
	{
		return p;
	}
	/**
	 * determines the maximum radius of any of the pathable objects contained in the group
	 * @return returns that largest radius of any pathable object in the group
	 */
	public double getMaxRadius()
	{
		return maxRadius;
	}
	/**
	 * sets the path of all elements in the group to the passed path
	 * @param path
	 */
	public void setPath(Path path)
	{
		for(Pathable pathable: p)
		{
			pathable.setPath(path);
		}
	}
}
