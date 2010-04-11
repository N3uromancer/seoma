package world.modifier;

import java.util.HashMap;

import pathfinder.graph.Node;


/**
 * defines an interface for objects that can be pathed through the world, all pathable
 * objects are bounded by circles
 * @author Jack
 *
 */
public interface Pathable extends Moveable
{
	public double getRadius();
	public HashMap<Integer, Node> getPath();
	public void setPath(HashMap<Integer, Node> path);
	/**
	 * gets the current node the pathable object is moving towards
	 * @return returns the index key for the node in the path map that
	 * the pathable object is currently moving towards
	 */
	public int getPathNodeIndex();
}
