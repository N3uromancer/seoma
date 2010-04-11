package world.modifier;

import geom.Boundable;

/**
 * defines obstacles that prevent pathable objects from moving in certain
 * regions of the world, only pathable objects can be blocked
 * @author Secondary
 *
 */
public interface PathObstacle extends Boundable
{
	/**
	 * determines the distance to the edge of the passed pathable
	 * object, used to determine potential fields for moving
	 * pathable objects through the world
	 * @param p
	 * @return returns the distance to the edge of the passed pathable object
	 */
	public double getDistance(Pathable p);
	/**
	 * determines if the passed pathable object intersects the path obstacle
	 * given its current velocity and radial bounds
	 * @param p
	 * @param tdiff the time difference, time step
	 * @return
	 */
	public boolean intersects(Pathable p, double tdiff);
}
