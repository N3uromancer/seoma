package world.modifier;

import world.World;

/**
 * defines an interface for objects that have to load some starter
 * variables or information from the server or client to be instantiated
 * correctly
 * @author Jack
 *
 */
public interface Initializable
{
	/**
	 * loads the initial state specified by the data in the byte buffer
	 * @param b
	 * @param w
	 */
	public void loadInitialState(byte[] b, World w);
	/**
	 * gets the initial starting state of the object, this starting information is
	 * sent to connected clients when the object is created, this method can be used
	 * to send constructor like information to network objects, the state must be represented
	 * in less than 256 bytes
	 * @return
	 */
	public abstract byte[] getInitialState();
}
