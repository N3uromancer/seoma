package world.networkUpdateable;

import world.World;

/**
 * defines a simple updateable interface to be sheduled with the world and updated,
 * updateables are meant for temporary occurences that span several world updates,
 * only the server should use updateables for core initialization mechanics (ex. creating units)
 * because updateables initialized in the past before a client joined will not be sent to
 * the new client
 * @author Jack
 *
 */
public interface Updateable
{
	public void update(World w, double tdiff);
	public boolean isDead();
}
