package world.initializer;

import java.util.HashMap;

import world.World;
import world.networkUpdateable.NetworkUpdateable;

/**
 * defines a class to initialize objects in the world, every implementing class
 * should have a byte array for its sole argument for its constructor, the length
 * of the byte array needed to properly execute an initialization action should
 * not exceed 256
 * 
 * the data necessary to initialize objects may change from client to server depending
 * on the arguments of the network updateables created, however an initializable object
 * must initialize the same network updateables on the client as on the server, if this
 * is not followed initializables the create multiple network updateable objects may
 * cause errors in the relevant sets because they will not be able to determine which
 * initialization orders to send to their associated clients
 * 
 * ex. three network updateables created on server with ids 1, 2, 3; three network udpateables
 * must be created on the client with ids 1, 2, 3
 * @author Jack
 *
 */
public interface Initializable
{
	/**
	 * performs the actual action of initialization of network objects and
	 * returns them for registration in the world
	 * @param w
	 * @return returns a hash map of network updateables, each object is mapped to the
	 * specific region it is to be registered with
	 */
	public HashMap<NetworkUpdateable, Byte> initialize(World w);
	/**
	 * gets the arguments necesary to run the initialization action, the arguments returned here
	 * are sent to the client
	 * @return returns a byte buffer containing the necessary information
	 * to execute the initialization action
	 */
	public byte[] getIniArgs();
}
