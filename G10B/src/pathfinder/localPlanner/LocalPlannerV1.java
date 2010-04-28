package pathfinder.localPlanner;

import geom.LineSegment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import pathfinder.StationaryPathable;
import pathfinder.graph.Graph;
import pathfinder.graph.Node;
import pathfinder.path.ApproachType;
import utilities.MathUtil;
import world.modifier.PathObstacle;
import world.modifier.Pathable;
import world.vehicle.Vehicle;

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
	public void movePathableObj(Pathable p, double[] target, ApproachType approach, HashSet<PathObstacle> obstacles, double tdiff)
	{
		double[] l = p.getLocation();
		double[] v = p.getVelocity();
		//p.clearForces();
		
		//double[] f = {(target[0]-l[0])*.1, (target[1]-l[1])*.1};
		double[] f = {target[0]-l[0], target[1]-l[1]};
		double fmag = MathUtil.magnitude(f);
		f[0]*=15/fmag;
		f[1]*=15/fmag;
		p.addForce(f);
		
		//for static obstacle collisions, constructs probes
		double ftime = .5; //time in the furture, how far it looks ahead for the collision
		LineSegment[] probes = Vehicle.generateProbes(p, ftime);
		
		//System.out.println("considered obstacles = "+obstacles.size());
		for(PathObstacle obstacle: obstacles)
		{
			double distance = obstacle.getDistance(p);
			if(obstacle instanceof Pathable)
			{
				if(distance == 0)
				{
					distance = 1;
				}
				double[] ol = obstacle.getLocation(); //obstacle location
				f = MathUtil.normal(l[0], l[1], ol[0], ol[1]);
				f[0]*=90/distance; //300
				f[1]*=90/distance;
				//f = new double[]{(l[0]-ol[0])*2/distance, (l[1]-ol[1])*2/distance};
				p.addForce(f);
			}
			else
			{
				/*if(obstacle.intersects(futureP, 0))
				{
					f = MathUtil.normal(l[0], l[1], newL[0], newL[1]);
					distance = obstacle.getDistance(futureP);
					f[0] += 400/distance;
					f[1] += 400/distance;
					p.addForce(f);
				}*/
				
				boolean intersects = false;
				for(int i = 0; i < probes.length && !intersects; i++)
				{
					intersects = obstacle.intersects(probes[i]);
				}
				if(intersects)
				{
					//System.out.println("intersects in future");
					//double[] ol = obstacle.getLocation();
					//f = MathUtil.normal(l[0], l[1], ol[0], ol[1]);
					f = MathUtil.normal(0, 0, v[0], v[1]);
					f[0]*=300/distance/distance;
					f[1]*=300/distance/distance;
					//p.clearForces();
					p.addForce(f);
					//System.out.println(f[0]+", "+f[1]);
				}
			}
		}
		
		f = p.getTotalForce();
		v[0]+=f[0];
		v[1]+=f[1];
		//v[0]=f[0]-v[0];
		//v[1]=f[1]-v[1];
		if(approach == ApproachType.Arrval)
		{
			double approachDistance = 100; //radius around target before slowing down
			double targetDistance = MathUtil.distance(l[0], l[1], target[0], target[1]);
			if(targetDistance < approachDistance)
			{
				//unit is inside approach zone
				v[0]*=.96;
				v[1]*=.96;
				
				double newSpeed = MathUtil.magnitude(v);
				if(targetDistance < approachDistance / 9 || newSpeed < p.getMaxSpeed()*.08)
				{
					v[0] = 0;
					v[1] = 0;
				}
				else
				{
					v[0]*=.87;
					v[1]*=.87;
					//v[0]*=Math.pow(targetDistance/approachDistance, .5);
					//v[1]*=Math.pow(targetDistance/approachDistance, .5);
					
					//clips the new speed to the previous velocity at max
					/*double oldSpeed = MathUtil.magnitude(p.getVelocity());
					if(newSpeed > oldSpeed)
					{
						double scalar = oldSpeed / newSpeed;
						v[0]*=scalar;
						v[1]*=scalar;
					}*/
				}
			}
		}
		p.setVelocity(v);
		p.updateLocation(tdiff);
		//System.out.println("--------------------------");
	}
}
