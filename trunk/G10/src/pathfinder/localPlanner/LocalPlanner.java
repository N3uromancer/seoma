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
	public void connectNodes(Set<Node> n, Graph g, HashSet<PathObstacle> obstacles);
	/**
	 * moves the passed pathable object through the world
	 * @param p the pathable object to be moved
	 * @param target the target location the pathable object is to be moved towards
	 * @param obstacles the path obstacles to be considered
	 * @param tdiff
	 */
	public void movePathableObj(Pathable p, double[] target, HashSet<PathObstacle> obstacles, double tdiff);
	/**
	 * tests to see if a connection exists between the two passed points, a connection
	 * exists if a pathable object of the passed radius can move between the two points
	 * using only the local
	 * planner
	 * @param start
	 * @param end
	 * @param radius the radius of the object to be pathed from the start point to the end point
	 * @param obstacles the path obstacles to be considered when testing the connection
	 * @return returns true if there exists a connection between the two points and only
	 * the local planner is needed to traverse the space in between
	 */
	public boolean connectionExists(double[] start, double[] end, double radius, HashSet<PathObstacle> obstacles);
}
