package pathfinder.nodeGenerator;

import java.util.HashSet;

import pathfinder.graph.Graph;
import world.modifier.PathObstacle;

/**
 * an interface defining a class capable of creating a base set of nodes to be used for pathfinding
 * @author Secondary
 *
 */
public interface NodeGenerator
{
	/**
	 * generates the nodes to be used as the pathfinding base
	 * @param g
	 * @param obstacles
	 * @param radii
	 * @param width
	 * @param height
	 * @param nodes
	 */
	public void generateNodes(Graph g, HashSet<PathObstacle> obstacles, double[] radii, double width, double height, int nodes);
}
