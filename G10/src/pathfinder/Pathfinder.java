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
import pathfinder.group.Group;
import pathfinder.group.PathableGrouper;
import pathfinder.localPlanner.LocalPlanner;
import pathfinder.nodeGenerator.NodeGenerator;
import utilities.MathUtil;
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
	private LocalPlanner lp;
	
	/**
	 * creates the pathfinder and generates an intitial graph
	 * @param obstacles an initial list of permanent non-moving path obstacles
	 * @param radii the different radii to generate nodes for
	 * @param width
	 * @param height
	 * @param ng the node generator to be used to create the base subset of nodes
	 */
	public Pathfinder(HashSet<PathObstacle> obstacles, double[] radii, double width, double height,
			NodeGenerator ng, LocalPlanner lp)
	{
		this.lp = lp;
		ng.generateNodes(g, obstacles, radii, width, height, 500);
		lp.connectNodes(g.getNodes(), g, obstacles);
	}
	/**
	 * gets a reference to the local planner to be used with the pathfinder
	 * @return returns the local planner
	 */
	public LocalPlanner getLocalPlanner()
	{
		return lp;
	}
	/**
	 * finds a path for a given set of pathable game objects to the passed target location,
	 * automatically sets the path for all passed pathable objects
	 * @param p
	 * @param target
	 */
	public void findPath(HashSet<Pathable> p, double[] target)
	{
		Group[] groups = PathableGrouper.groupPathables(p, 60, 6);
		Node end = null;
		for(Group group: groups)
		{
			double startMinDist = Double.MAX_VALUE;
			double endMinDist = Double.MAX_VALUE;
			Node start = null;
			boolean findEnd = false;
			if(end == null)
			{
				findEnd = true;
			}
			for(Node n: g.getNodes())
			{
				double[] center = group.getCenter();
				double distance = MathUtil.distance(n.l[0], n.l[1], center[0], center[1]);
				if(distance < startMinDist)
				{
					startMinDist = distance;
					start = n;
				}
				if(findEnd)
				{
					distance = MathUtil.distance(n.l[0], n.l[1], target[0], target[1]);
					if(distance < endMinDist)
					{
						endMinDist = distance;
						end = n;
					}
				}
			}
			if(start != null && end != null)
			{
				Stack<Node> stackPath = findPath(start, end, g, group.getMaxRadius());
				HashMap<Integer, Node> path = new HashMap<Integer, Node>();
				int index = 0;
				while(stackPath.size() > 0)
				{
					path.put(index, stackPath.pop());
					index++;
				}
				group.setPath(path);
			}
		}
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
		buildPath(path, fails, end, g);
		return path;
	}
	/**
	 * iteratively builds the path
	 * @param path the path
	 * @param checked a set represnting the already tested nodes not to be checked
	 * @param target the target of the path
	 * @param end the end node where the target resides
	 * @param g
	 */
	private static void buildPath(Stack<Node> path, HashSet<Node> checked, final Node end, Graph g)
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
			
			int x = (int)(n.l[0]-n.radius);
			int y = (int)(n.l[1]-n.radius);
			g.fillOval(x, y, (int)n.radius*2, (int)n.radius*2);
			//System.out.println(n.radius+"\n-----------");
			
			for(Node temp: this.g.getEdges().get(n))
			{
				g.drawLine((int)n.l[0], (int)n.l[1], (int)temp.l[0], (int)temp.l[1]);
			}
		}
	}
}
