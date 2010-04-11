package pathfinder.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class Graph
{
	HashMap<Node, HashSet<Node>> edges = new HashMap<Node, HashSet<Node>>(); //key=node, value=set directed edges
	
	/**
	 * adds a node to the graph
	 * @param t
	 */
	public void addNode(Node t)
	{
		edges.put(t, new HashSet<Node>());
	}
	/**
	 * adds a directed edge to the graph, passed nodes must
	 * already be in the graph
	 * @param t1
	 * @param t2
	 */
	public void addEdge(Node t1, Node t2)
	{
		edges.get(t1).add(t2);
		//edges.get(t2).add(t1);
	}
	public HashMap<Node, HashSet<Node>> getEdges()
	{
		return edges;
	}
	public Set<Node> getNodes()
	{
		return edges.keySet();
	}
}
