package world.modifier;

import world.World;

/**
 * defines an interface for objects that can be updated over the network
 * by either the server or a client
 * @author Jack
 *
 */
public abstract class NetworkUpdateable implements Locateable
{
	private short id;
	private byte type;
	private int updatePriority;
	private boolean ready = false;
	/**
	 * represents the objects state in regard to the control the engine has
	 * over it, if true then the object is a ghost object and its state is
	 * updated from another source
	 */
	private boolean isGhost;
	
	public NetworkUpdateable(boolean isGhost, short id, byte type, int updatePriority)
	{
		this.isGhost = isGhost;
		this.id = id;
		this.type = type;
		this.updatePriority = updatePriority;
	}
	/**
	 * loads the state specified by the data in the byte buffer
	 * @param b
	 */
	public abstract void loadState(byte[] b);
	/**
	 * gets the current state of the object
	 * @return returns a byte buffer containing the current state of the object
	 */
	public abstract byte[] getState();
	/**
	 * gets the id assigned to this network updateable object, each object
	 * has a unique id
	 * @return returns the unique id associated with this network object
	 */
	public short getID()
	{
		return id;
	}
	/**
	 * checks to see if this object is a ghost copy or an original, typically
	 * objects on the server are originals while objects on clients are ghosts,
	 * only non ghost objects are updated
	 * @return
	 */
	public boolean isGhost()
	{
		return isGhost;
	}
	/**
	 * updates the object, meant only to be called on non ghost objects
	 * @param w
	 * @param tdiff
	 */
	public abstract void update(World w, double tdiff);
	/**
	 * simulates an update to the object, a simulated update is not the same as
	 * a real update, ghost objects are simulated in between receiving update information
	 * from the server
	 * @param w
	 * @param tdiff
	 */
	public abstract void simulate(World w, double tdiff);
	/**
	 * gets the base update priority for the game object for determining
	 * the relevant set, update priority is dynamic and non fixed and is
	 * determined at the discretion of the game object itself, an object
	 * with a higher update priority will be updated more often than one
	 * with a lower priority
	 * @return returns the update priority for the object
	 */
	public int getUpdatePriority()
	{
		return updatePriority;
	}
	/**
	 * cheacks to see if the network updateable object has all the information
	 * it needs to function, only after is has recevied its information should it be used
	 * @return returns true if the the object is ready to be used, false otherwise
	 */
	public boolean isReady()
	{
		return ready;
	}
	/**
	 * declares the object to be ready for use in the world
	 */
	protected void setReady()
	{
		ready = true;
	}
	/**
	 * gets the type of the object, used to determine which class of object
	 * to instantiate when receiving spawn orders from the server
	 * @return returns the type of the object
	 */
	public byte getType()
	{
		return type;
	}
}
