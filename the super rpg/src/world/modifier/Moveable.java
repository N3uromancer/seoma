package world.modifier;

import geom.Boundable;

import java.util.HashSet;

import world.terrain.Terrain;

/**
 * defines a marker interface for objects that can move around inside the region
 * they are associated with, moveable objects must be declared to be so with this
 * interface to ensure that they are properly handled by the drawign routine
 * @author Jack
 *
 */
public interface Moveable extends Locateable, Boundable
{
	/**
	 * gets the set of terrain types that the object can move over
	 * @return returns the movement type of the movable object
	 */
	public HashSet<Terrain> getMovementType();
}