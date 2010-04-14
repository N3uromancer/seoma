package world.modifier;

import java.io.DataOutputStream;
import java.nio.ByteBuffer;

/**
 * represents a game object, everything in the game must be an
 * instance of this object
 * @author Jack
 *
 */
public abstract class GameObject
{
	/**
	 * dynamic id for the game object, only non-permanent objects
	 * are assigned an id, id used for referencing it for updates
	 */
	private short id;
	/**
	 * writes the objects state to the output stream
	 */
	public abstract void writeState(DataOutputStream dos);
	/**
	 * reads and updates the game object's state from the byte buffer
	 * @param buff
	 */
	public abstract void readState(ByteBuffer buff);
	/**
	 * gets the id of the game object
	 * @return returns the id of the object
	 */
	public short getID()
	{
		return id;
	}
	/**
	 * sets the id for this game object, only to be called by the world
	 * @param id
	 */
	public void setID(short id)
	{
		this.id = id;
	}
	/**
	 * gets the base update priority for the game object for determining
	 * the relevant set, update priority is dynamic and non fixed and is
	 * determined at the discretion of the game object itself, an object
	 * with a higher update priority will be updated more often than one
	 * with a lower priority
	 * @return returns the update priority for the object
	 */
	public abstract int getUpdatePriority();
}
