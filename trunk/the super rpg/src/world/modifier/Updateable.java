package world.modifier;

import world.World;

/**
 * interface for objects that should be updated, only non-ghost objects are updated
 * @author Jack
 *
 */
public interface Updateable
{
	public void update(World w, double tdiff);
}
