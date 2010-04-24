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
	 * gets the current velocity of the object
	 * @return returns the current velocity of the object
	 */
	public double[] getVelocity();
	/**
	 * sets the velocity of the moveable object
	 * @param v
	 */
	public void setVelocity(double[] v);
}
