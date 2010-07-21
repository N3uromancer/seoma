package world.initializer;

import world.World;

/**
 * defines a class to initialize objects in the world
 * @author Jack
 *
 */
public interface Initializable
{
	/**
	 * performs the actual action of initialization and properly registers
	 * newly created objects with the world
	 * @param args the arguments to create the objects, cannot be more than 256 bytes
	 * @param w
	 */
	public void initialize(byte[] args, World w);
	/**
	 * tests to determine if the action of initializing the object is relevant
	 * to the specified network object, non relevant objects are not sent to
	 * connected clients
	 * @param id the id of the network object
	 * @param w
	 * @return returns true if the initialization action is relevant, false otherwise
	 */
	public boolean isRelevant(short id, World w);
	/**
	 * tests to determine if the action of initializing the object is immediately
	 * relevant to the client and should be sent immediately, otherwise the relevant
	 * set will hold the initializeable data
	 * @param id
	 * @param w
	 * @return
	 */
	public boolean immediatelyRelevant(short id, World w);
}
