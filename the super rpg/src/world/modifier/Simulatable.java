package world.modifier;

import world.World;

/**
 * interface for objects that should be simulated, only ghost objects are simulated
 * @author Jack
 *
 */
public interface Simulatable
{
	public void simulate(World w, double tdiff);
}
