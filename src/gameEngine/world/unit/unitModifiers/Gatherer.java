package gameEngine.world.unit.unitModifiers;

/**
 * defines a unit that can gather resources
 * @author Jack
 *
 */
public interface Gatherer
{
	/**
	 * gets the maximum amount the gatherer can carry
	 * @return
	 */
	public double getMaxLoad();
	/**
	 * gets the amount that the gatherer currently has
	 * @return
	 */
	public double getLoad();
	/**
	 * sets the load of the gatherer to the passed amount
	 * @param setter
	 */
	public void setLoad(double setter);
	/**
	 * gets the rate at which the gatherer is capable of loading things
	 * @return
	 */
	public double getGatherRate();
}
