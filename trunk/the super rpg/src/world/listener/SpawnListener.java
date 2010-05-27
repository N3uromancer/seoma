package world.listener;

import world.modifier.NetworkUpdateable;
import world.region.Region;

/**
 * listens for newly instantiated network objects
 * @author Jack
 *
 */
public interface SpawnListener
{
	/**
	 * signifies a new object has been registered with the World
	 * @param o the object that was created
	 * @param r the region the object was associated with
	 */
	public void objectSpawned(NetworkUpdateable o, Region r);
	/**
	 * called when an object is destroyed and subsequently removed from the game world
	 * @param o the object that was destroyed
	 * @param r the region the object was associated with
	 */
	public void objectDestroyed(NetworkUpdateable o, Region r);
}
