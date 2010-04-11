package world.modifier;

import geom.Boundable;
import world.World;

/**
 * defines an object that can be destroyed by receiving damage
 * @author Secondary
 *
 */
public interface Destructable extends Temporary, Boundable
{
	/**
	 * called after the object has been declared dead
	 * @param w
	 */
	public void destroy(World w);
	public double getLife();
	public void setLife(double setter);
}
