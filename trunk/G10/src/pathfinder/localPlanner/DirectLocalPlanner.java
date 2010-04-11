package pathfinder.localPlanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import pathfinder.StationaryPathable;
import pathfinder.graph.Graph;
import pathfinder.graph.Node;
import world.modifier.PathObstacle;
import world.modifier.Pathable;

/**
 * determines local paths between pathing nodes only by interpolating
 * points along a line connecting pathing nodes and testing for intersections
 * 
 * moves units by potential fields between connected nodes
 * @author Jack
 *
 */
public class DirectLocalPlanner implements LocalPlanner
{
	private static double distance(double[] l1, double[] l2)
	{
		double x = l1[0]-l2[0];
		double y = l1[1]-l2[1];
		return Math.sqrt(x*x+y*y);
	}
	/**
	 * checks the line between the two passed points to see if it is clear of obstacles
	 * @param l1
	 * @param l2
	 * @param radius
	 * @param obstacles
	 * @param step the distance between each tested position interpolated on the line
	 * @return returns true if the node can be connected to another node
	 */
	private static boolean connects(double[] l1, double[] l2, double radius, PathObstacle[] obstacles, double step)
	{
		double[] v = {l2[0]-l1[0], l2[1]-l1[1]};
		
		double distance = distance(l1, l2);
		v = new double[]{v[0]/distance, v[1]/distance};
		
		//the 0 and ending index are the nodes, which have already been tested for intersections
		for(double i = step; i < distance; i+=step)
		{
			double[] l = {l1[0]+v[0]*i, l1[1]+v[1]*i};
			Pathable p = new StationaryPathable(l, radius);
			
			for(int a = 0; a < obstacles.length; a++)
			{
				if(obstacles[a].intersects(p, 0))
				{
					return false;
				}
			}
		}
		return true;
	}
	public void connectNodes(Node[] nodes, Graph g, PathObstacle[] obstacles)
	{
		HashMap<Node, PriorityQueue<Double>> neighborDistances = new HashMap<Node, PriorityQueue<Double>>();
		HashMap<Node, HashMap<Double, Node>> neighbors = new HashMap<Node, HashMap<Double, Node>>();
		int closestNeighbors = 6; //number of close nodes each node is to attempt to connect to
		
		//order closests nodes
		for(Node n: nodes)
		{
			neighborDistances.put(n, new PriorityQueue<Double>());
			neighbors.put(n, new HashMap<Double, Node>());
			for(Node temp: nodes)
			{
				if(n != temp && temp.radius <= n.radius)
				{
					double distance = distance(n.l, temp.l);
					neighborDistances.get(n).add(distance);
					neighbors.get(n).put(distance, temp);
				}
			}
		}
		//connect k closests nodes
		double step = 10;
		for(Node n: nodes)
		{
			PriorityQueue<Double> q = neighborDistances.get(n);
			int ncount = 0; //closest neighbor count
			while(q.size() > 0 && ncount < closestNeighbors)
			{
				double d = q.poll();
				Node temp = neighbors.get(n).get(d);
				if(connects(n.l, temp.l, n.radius, obstacles, step))
				{
					g.addEdge(n, temp);
					ncount++;
				}
			}
		}
	}
	public void movePathableObj(Pathable p, HashSet<PathObstacle> obstacles)
	{
		
	}
}
