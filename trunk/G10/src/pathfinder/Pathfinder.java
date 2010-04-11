package pathfinder;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

import pathfinder.graph.Graph;
import pathfinder.graph.Node;
import pathfinder.localPlanner.LocalPlanner;
import pathfinder.nodeGenerator.NodeGenerator;
import world.modifier.PathObstacle;
import world.modifier.Pathable;

/**
 * determines the graph representing the free space of the world and
 * calculates paths for the objects through the world
 * @author Secondary
 *
 */
public final class Pathfinder
{
	private Graph g = new Graph();
	
	/**
	 * creates the pathfinder and generates an intitial graph
	 * @param obstacles an initial list of permanent non-moving path obstacles
	 * @param radii the different radii to generate nodes for
	 * @param width
	 * @param height
	 * @param ng the node generator to be used to create the base subset of nodes
	 */
	public Pathfinder(PathObstacle[] obstacles, double[] radii, double width, double height,
			NodeGenerator ng, LocalPlanner lp)
	{
		ng.generateNodes(g, obstacles, radii, width, height, 500);
	}
	/**
	 * finds a path for a given set of pathable game objects to the passed target location
	 * @param p
	 * @param target
	 * @return
	 */
	public HashMap<Integer, Node> findPath(HashSet<Pathable> p, double[] target)
	{
		double minDist = Double.MAX_VALUE;
		for(Node n: g.getNodes())
		{
			//if(MathUtil.distance(n.l[0], n.l[1], p.getLocation()[0], p.getLocation()[1]))
		}
		
		return null;
	}
	/**
	 * actually finds a path through the graph from the passed start node to
	 * the passed end node
	 * @param start
	 * @param end
	 * @param g
	 * @param minRadius the minimum radius nodes in the path must have
	 * @return returns a stack of nodes representing the path
	 */
	private Stack<Node> findPath(Node start, Node end, Graph g, double minRadius)
	{
		Stack<Node> path = new Stack<Node>();
		path.push(start);
		HashSet<Node> fails = new HashSet<Node>();
		getPath(path, fails, end, g);
		return path;
	}
	/**
	 * recursively builds the path
	 * @param path the path
	 * @param checked a set represnting the already tested nodes not to be checked
	 * @param target the target of the path
	 * @param end the end node where the target resides
	 * @param g
	 */
	private static void getPath(Stack<Node> path, HashSet<Node> checked, final Node end, Graph g)
	{
		while(path.size() > 0 && path.peek() != end)
		{
			Node n = path.peek();
			checked.add(n);
			HashSet<Node> edges = g.getEdges().get(n);
			Comparator<Node> c = new Comparator<Node>()
			{
				public int compare(Node n1, Node n2)
				{
					Double n1d = distance(n1.l, end.l);
					Double n2d = distance(n2.l, end.l);
					return n1d.compareTo(n2d);
				}
				private double distance(double[] l1, double[] l2)
				{
					double x = l1[0]-l2[0];
					double y = l1[1]-l2[1];
					return Math.sqrt(x*x+y*y);
				}
			};
			PriorityQueue<Node> q = new PriorityQueue<Node>(edges.size(), c);
			for(Node adj: edges)
			{
				if(!checked.contains(adj))
				{
					q.add(adj);
				}
			}
			if(q.size() == 0)
			{
				path.pop();
			}
			else
			{
				while(q.size() > 0)
				{
					Node adj = q.poll();
					if(g.getEdges().get(adj).contains(end))
					{
						path.push(adj);
						path.push(end);
						break;
					}
					else if(g.getEdges().get(adj).size() == 1)
					{
						//only has an edge leading back to this node, dead end
						checked.add(adj);
					}
					else
					{
						path.push(adj);
						break;
					}
				}
			}
		}
	}
	public void drawPathGraph(Graphics2D g)
	{
		Set<Node> nodes = this.g.getNodes();
		for(Node n: nodes)
		{
			g.setColor(Color.black);
			
			//new Circle(n.l, n.radius).draw(g);
			int x = (int)(n.l[0]-n.radius);
			int y = (int)(n.l[1]-n.radius);
			g.fillOval(x, y, (int)n.radius*2, (int)n.radius*2);
			
			for(Node temp: this.g.getEdges().get(n))
			{
				g.drawLine((int)n.l[0], (int)n.l[1], (int)temp.l[0], (int)temp.l[1]);
			}
		}
	}
}
