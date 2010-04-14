package pathfinder.graph;

public final class Node
{
	public double[] l;
	public double radius;
	
	/**
	 * creats a new pathing node
	 * @param l
	 * @param radius the largest radius that an object can have
	 * and move correctly through the node
	 */
	public Node(double[] l, double radius)
	{
		this.l = l;
		this.radius = radius;
	}
}
