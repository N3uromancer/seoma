package pathfinder.localPlanner;

import java.util.HashSet;
import java.util.Set;

import pathfinder.graph.Graph;
import pathfinder.graph.Node;
import world.modifier.PathObstacle;
import world.modifier.Pathable;

/**
 * responsible for connecting paths and moving pathable objects along their paths
 * @author Secondary
 *
 */
public interface LocalPlanner
{
	/**
	 * connecst the passed nodes to other nodes in the graph if possible
	 * @param n the nodes to be connected to other nodes in the graph, these nodes
	 * should have already been added to the graph
	 * @param g
	 * @param obstacles the base obstacles of the environement to be considered when
	 * connecting the passed pathing nodes
	 */
	public void connectNodes(Set<Node> n, Graph g, PathObstacle[] obstacles);
	/**
	 * moves the passed pathable object through the world
	 * @param p the pathable object to be moved
	 * @param obstacles the path obstacles in the world
	 * @param tdiff
	 */
	public void movePathableObj(Pathable p, HashSet<PathObstacle> obstacles, double tdiff);
}
