package world.modifier;

/**
 * defines an interface for objects capable of moving through the world
 * @author Jack
 *
 */
public interface Moveable extends Locateable
{
	/**
	 * determines if the object is moving this iteration, non-moving
	 * objects are not removed from their partitions
	 * @return returns true if the object is moving, false otherwise
	 */
	public boolean isMoving();
	/**
	 * gets the target location that the object is moving towards
	 * @return returns the movment target for the object
	 */
	public double[] getTarget();
	/**
	 * returns the maximum movement speed of the object
	 * @return returns the max movement amount of the target per second
	 */
	public double getMaxSpeed();
	/**
	 * gets the current velocity of the object
	 * @return returns the current velocity of the object
	 */
	public double[] getVelocity();
}
