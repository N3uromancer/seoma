package world.modifier;

/**
 * defines an interface for objects that can be updated over the network
 * by either the server or a client
 * @author Jack
 *
 */
public interface NetworkUpdateable extends Locateable, Updateable
{
	/**
	 * loads the state specified by the data in the byte buffer
	 * @param b
	 */
	public void loadState(byte[] b);
	/**
	 * gets the current state of the object
	 * @return returns a byte buffer containing the current state of the object
	 */
	public byte[] getState();
	/**
	 * gets the id assigned to this network updateable object, each object
	 * has a unique id
	 * @return returns the unique id associated with this network object
	 */
	public short getID();
	/**
	 * sets the id of the network updateable object
	 * @param id
	 */
	public void setID(short id);
	/**
	 * gets the base update priority for the game object for determining
	 * the relevant set, update priority is dynamic and non fixed and is
	 * determined at the discretion of the game object itself, an object
	 * with a higher update priority will be updated more often than one
	 * with a lower priority
	 * @return returns the update priority for the object
	 */
	public int getUpdatePriority();
	/**
	 * cheacks to see if the network updateable object has all the information
	 * it needs to function, only after is has recevied its information should it be used
	 * @return returns true if the the object is ready to be used, false otherwise
	 */
	public boolean isReady();
}
