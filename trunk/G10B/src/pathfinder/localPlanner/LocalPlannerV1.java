package pathfinder.localPlanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import pathfinder.StationaryPathable;
import pathfinder.graph.Graph;
import pathfinder.graph.Node;
import utilities.MathUtil;
import world.modifier.PathObstacle;
import world.modifier.Pathable;

/**
 * determines local paths between pathing nodes and locations
 * @author Jack
 *
 */
public class LocalPlannerV1 implements LocalPlanner
{
	private static double distance(double[] l1, double[] l2)
	{
		double x = l1[0]-l2[0];
		double y = l1[1]-l2[1];
		return Math.sqrt(x*x+y*y);
	}
	public boolean connectionExists(double[] l1, double[] l2, double radius, HashSet<PathObstacle> obstacles)
	{
		double step = 10;
		double[] v = {l2[0]-l1[0], l2[1]-l1[1]};
		
		double distance = distance(l1, l2);
		v = new double[]{v[0]/distance, v[1]/distance};
		
		//the 0 and ending index are the nodes, which have already been tested for intersections
		for(double i = step; i < distance; i+=step)
		{
			double[] l = {l1[0]+v[0]*i, l1[1]+v[1]*i};
			Pathable p = new StationaryPathable(l, radius);
			
			for(PathObstacle obstacle: obstacles)
			{
				if(obstacle.intersects(p, 0))
				{
					return false;
				}
			}
		}
		return true;
	}
	public void connectNodes(Set<Node> nodes, Graph g, HashSet<PathObstacle> obstacles)
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
		for(Node n: nodes)
		{
			PriorityQueue<Double> q = neighborDistances.get(n);
			int ncount = 0; //closest neighbor count
			while(q.size() > 0 && ncount < closestNeighbors)
			{
				double d = q.poll();
				Node temp = neighbors.get(n).get(d);
				if(connectionExists(n.l, temp.l, n.radius, obstacles))
				{
					g.addEdge(n, temp);
					ncount++;
				}
			}
		}
	}
	public void movePathableObj(Pathable p, double[] target, HashSet<PathObstacle> obstacles, double tdiff)
	{
		double[] l = p.getLocation();
		//double[] f = {(target[0]-l[0])*.1, (target[1]-l[1])*.1};
		double[] f = {target[0]-l[0], target[1]-l[1]};
		double fmag = MathUtil.magnitude(f);
		f[0]*=15/fmag;
		f[1]*=15/fmag;
		p.addForce(f);
		
		//System.out.println("considered obstacles = "+obstacles.size());
		for(PathObstacle obstacle: obstacles)
		{
			double distance = obstacle.getDistance(p);
			if(obstacle instanceof Pathable)
			{
				double[] ol = obstacle.getLocation(); //obstacle location
				f = new double[]{(l[0]-ol[0])*2/distance, (l[1]-ol[1])*2/distance};
				p.addForce(f);
			}
			else
			{
				//use probes for static obstacles
				/*double[] ol = obstacle.getLocation(); //obstacle location
				double fx = Math.pow((l[0]-ol[0]), 1)/distance;
				double fy = Math.pow((l[1]-ol[1]), 1)/distance;
				f = new double[]{fx, fy};
				p.addForce(f);*/
			}
		}
		
		double[] v = p.getVelocity();
		f = p.getTotalForce();
		v[0]+=f[0];
		v[1]+=f[1];
		double approachDistance = 150; //radius around target before slowing down
		double targetDistance = MathUtil.distance(l[0], l[1], target[0], target[1]);
		if(targetDistance < approachDistance)
		{
			v[0]*=Math.pow(targetDistance/approachDistance, .5);
			v[1]*=Math.pow(targetDistance/approachDistance, .5);
		}
		p.setVelocity(v);
		p.updateLocation(tdiff);
	}
	/**
	 * gets the potential for a given configuration, if the configuration is intersecting
	 * either a unit or obstacle its potential is the maximum value of a double
	 * 
	 * potential is zero if the passed circle to be evaluated is within the minimum distance
	 * to the target path point node
	 * 
	 * @param p the pathable object 
	 * @param mover the pathable object currently being moved
	 * @param obstacles the pathing obstacles to be considered when determining potential for a point
	 * @param target
	 * @param minDist the minimum distance from the pathable object to the target node before a potential
	 * of 0 is returned
	 * @return
	 */
	private double getPotential(Pathable p, Pathable mover, HashSet<PathObstacle> obstacles, double[] target, double minDist)
	{
		//long start = System.currentTimeMillis();
		double potential = 0;
		for(PathObstacle obstacle: obstacles)
		{
			if(obstacle != mover)
			{
				if(obstacle.intersects(p, 0))
				{
					//System.out.println("intersects "+obstacle.getClass().getSimpleName());
					return Double.MAX_VALUE;
				}
				double distance = obstacle.getDistance(p);
				if(obstacle.getClass().isInstance(Pathable.class))
				{
					//another pathable object
					Pathable unit = (Pathable)obstacle;
					if(unit.getPriority() > p.getPriority()) //lower priority units yield to higher priority units
					{
						if(unit.isMoving())
						{
							potential+=600/distance;
						}
						else
						{
							potential+=100/distance;
						}
					}
				}
				else
				{
					//a normal path obstacle --> background terrain, etc
					potential+=1000./distance;
				}
			}
		}
		double distance = MathUtil.distance(target[0], target[1], p.getLocation()[0], p.getLocation()[1])-p.getRadius();
		if(distance < minDist)
		{
			return 0;
		}
		potential+=distance*distance;
		//System.out.println(System.currentTimeMillis()-start);
		return potential;
	}
}
