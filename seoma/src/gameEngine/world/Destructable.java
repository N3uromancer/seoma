package gameEngine.world;

/**
 * represents anything in the game world that is destructable
 * @author Jack
 *
 */
public interface Destructable
{
	/**
	 * sets the life of the desctructable to the passed amount
	 * @param setter the new life of the desctructable
	 */
	public void setLife(double setter);
	/**
	 * returns the current life of the desctructable
	 * @return returns the current life of the desctructable
	 */
	public double getLife();
	/**
	 * returns the state of the desctructable
	 * @return returns true when the desctructable is dead and needs to be destroyed
	 * false otherwise
	 */
	public boolean isDead();
	/**
	 * the action to be performed when the desctructable is destroyed
	 * @param w a reference to the game world
	 */
	public void destroy(World w);
}
