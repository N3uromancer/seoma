package world.initializer;

import world.World;

/**
 * defines a class to initialize objects in the world, every implementing class
 * should have a byte array for its sole argument for its constructor, the length
 * of the byte array needed to properly execute an initialization action should
 * not exceed 256
 * @author Jack
 *
 */
public interface Initializable
{
	/**
	 * performs the actual action of initialization and properly registers
	 * newly created objects with the world
	 * @param w
	 * @return returns any new initialization arguments, this is for initialization order
	 * like unit creation where the arguments necessary to initialize vary between the
	 * client and server, the byte buffer returned by this method will be sent as the
	 * arguements for initialization to registered relevant sets
	 */
	public byte[] initialize(World w);
	/**
	 * tests to determine if the action of initializing the object is relevant
	 * to the specified network object, non relevant objects are not sent to
	 * connected clients
	 * @param id the id of the unit for whom the initialize action is to be tested
	 * for relevancy towards
	 * @param w
	 * @return returns true if the initialization action is relevant, false otherwise
	 * indicating that the initializable action can be ignored
	 */
	public boolean isRelevant(short id, World w);
	/**
	 * tests to determine if the action of initializing the object is immediately
	 * relevant to the client and should be sent immediately, otherwise the relevant
	 * set will hold the initializeable data
	 * @param id the id of the unit for whom the initialize action is to be tested
	 * for immediate relevancy towards
	 * @param w
	 * @return returns true if the initialization action is immediately relevant, false otherwise
	 */
	public boolean immediatelyRelevant(short id, World w);
	/**
	 * checks to see if the initialize action needs to be broadcast to clients defaultly,
	 * if a broadcast order is not expressly given the initialization may or may not be
	 * broadcast to clients depending on their needs, nothing should ever strictly speaking
	 * need to be broadcast to clients in order to garentee proper game functionality
	 * (ex. attack orders will be registered as broadcast, however the damage is server side
	 * so whether or not they are broadcast the damage they do is still inflicted), typically
	 * initializables that create updateables should be broadcast to clients, initializations
	 * that create network updateables will only be broadcast to clients if the objects they
	 * create are deemed relevant for a specific client
	 * @return returns true if the initialize action needs to be forwarded to clients, false otherwise
	 */
	public boolean broadcast();
	/**
	 * gets the arguments necesary to run the initialization action
	 * @return
	 */
	public byte[] getIniArgs();
}
