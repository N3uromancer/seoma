package pathfinder.path;

import java.util.HashMap;

import pathfinder.graph.Node;

/**
 * represents a path to be followed by pathable objects through the world
 * @author Secondary
 *
 */
public final class Path
{
	private HashMap<Integer, Node> path;
	private int index;
	
	public Path(HashMap<Integer, Node> path)
	{
		this.path = path;
	}
	/**
	 * gets the path map representing the nodes and the order
	 * they are to be traversed
	 * @return returns the path
	 */
	public HashMap<Integer, Node> getPath()
	{
		return path;
	}
	/**
	 * gets the current node the pathable object is moving towards
	 * @return returns the index key for the node in the path map that
	 * the pathable object is currently moving towards
	 */
	public int getPathNodeIndex()
	{
		return index;
	}
	public void incrementPathNodeIndex()
	{
		index++;
	}
}
