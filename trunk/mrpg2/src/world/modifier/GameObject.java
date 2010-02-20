package world.modifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * represents a game object, everything in the game must be an
 * instance of this object
 * @author Jack
 *
 */
public abstract class GameObject
{
	public GameObject(){}
	/**
	 * creates a new game object whose state is loaded from the input stream
	 * as defined by the overriden read method
	 * @param dis
	 * @throws IOException
	 */
	public GameObject(DataInputStream dis) throws IOException
	{
		read(dis);
	}
	/**
	 * saves the state of the game object to the data stream
	 * @param dos
	 * @throws IOException
	 */
	public abstract void write(DataOutputStream dos) throws IOException;
	/**
	 * overridden in subclasses, reads and loads the state of the game object
	 * from the data stream
	 * @param dis
	 * @throws IOException
	 */
	public abstract void read(DataInputStream dis) throws IOException;
}
