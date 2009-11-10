package gameEngine.world;

/**
 * represents a world element that is to be updated
 * @author Jack
 *
 */
public interface Updateable
{
	/**
	 * updates the object
	 * @param w a reference to the game world
	 */
	public void update(World w);
	/**
	 * returns whether or not this object is to be removed
	 * from the main update loop
	 * @return returns true if the object is to be removed,
	 * false otherwise
	 */
	public boolean isDead();
	/**
	 * called when the object is removed from the main upate loop
	 * @param w a reference to the game world
	 */
	public void destroy(World w);
}
