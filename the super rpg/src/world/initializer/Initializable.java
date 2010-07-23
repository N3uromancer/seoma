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
	 * @return returns any new initialization arguments, this is for initialization order
	 * like unit creation where the arguments necessary to initialize vary between the
	 * client and server, the byte buffer returned by this method will be sent as the
	 * arguements for initialization to registered relevant sets
	 */
	public byte[] initialize(byte[] args, World w);
	/**
	 * tests to determine if the action of initializing the object is relevant
	 * to the specified network object, non relevant objects are not sent to
	 * connected clients
	 * @param id the id of the unit for whom the initialize action is to be tested
	 * for relevancy towards
	 * @param args
	 * @param w
	 * @return returns true if the initialization action is relevant, false otherwise
	 * indicating that the initializable action can be ignored
	 */
	public boolean isRelevant(short id, byte[] args, World w);
	/**
	 * tests to determine if the action of initializing the object is immediately
	 * relevant to the client and should be sent immediately, otherwise the relevant
	 * set will hold the initializeable data
	 * @param id the id of the unit for whom the initialize action is to be tested
	 * for immediate relevancy towards
	 * @param args
	 * @param w
	 * @return returns true if the initialization action is immediately relevant, false otherwise
	 */
	public boolean immediatelyRelevant(short id, byte[] args, World w);
	/**
	 * checks to see if the initialize action should be broadcast to clients
	 * @return returns true if the initialize action should be forwarded to clients, false otherwise
	 */
	public boolean broadcast();
}
