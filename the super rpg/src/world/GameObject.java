package world;


/**
 * represents a game object, everything in the game must be an
 * instance of this object
 * @author Jack
 *
 */
public abstract class GameObject
{
	/**
	 * represents the objects state in regard to the control the engine has
	 * over it, if true then the object is a ghost object and its state is
	 * updated from another source
	 */
	private boolean isGhost;
	/**
	 * creates a new game object
	 * @param isGhost true if the object is a ghost copy, false if it is real
	 */
	public GameObject(boolean isGhost)
	{
		this.isGhost = isGhost;
	}
	public boolean isGhost()
	{
		return isGhost;
	}
}
