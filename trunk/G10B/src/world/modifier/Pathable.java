package world.modifier;

import pathfinder.Path;


/**
 * defines an interface for objects that can be pathed through the world, all pathable
 * objects are bounded by circles
 * @author Jack
 *
 */
public interface Pathable extends Moveable
{
	public double getRadius();
	public void setPath(Path path);
	public void setPriority(double priority);
	public double getPriority();
	/**
	 * updates the location of the pathable object based off
	 * its velocity and the time differential
	 * @param tdiff
	 */
	public void updateLocation(double tdiff);
	public Path getPath();
	/**
	 * clears the forces on the pathable object
	 */
	public void clearForces();
	/**
	 * adds a force to the pathable object's force accumulator
	 * @param f
	 */
	public void addForce(double[] f);
	/**
	 * gets the cummulative forces on the object, represents
	 * a steering vector to be applied to the velocity
	 * @return
	 */
	public double[] getTotalForce();
}
