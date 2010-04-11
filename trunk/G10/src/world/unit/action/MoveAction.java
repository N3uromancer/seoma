package world.unit.action;

import java.util.HashMap;

import pathfinder.graph.Node;
import world.World;
import world.modifier.Pathable;

/**
 * an action primitive that moves a unit
 * @author Secondary
 *
 */
public final class MoveAction implements Action
{
	private Pathable p;
	private HashMap<Integer, Node> path;
	
	public MoveAction(Pathable p, HashMap<Integer, Node> path)
	{
		this.p = p;
		this.path = path;
	}
	public boolean performAction(World w, double tdiff)
	{
		//double tdiff, Unit[] units, Circle[] obstacles
		double movement = this.movement*tdiff;
		//System.out.println("movement = "+movement);
		if(path != null && pindex < path.size())
		{
			isMoving = true;
			//System.out.println("pindex="+pindex);
			PointNode target = path.get(pindex);
			double initialPotential = getPotential(this, obstacles, units, target, pindex == path.size()-1);
			//System.out.println("initial potential = "+initialPotential);
			
			double[] v = {target.l[0]-l[0], target.l[1]-l[1]};
			double mag = Math.sqrt(v[0]*v[0]+v[1]*v[1]);
			v[0]/=mag;
			v[1]/=mag;
			double[] newL = {l[0]+v[0]*movement, l[1]+v[1]*movement};
			double potential = getPotential(new Circle(newL, radius), obstacles, units, target, pindex == path.size()-1); //current potential
			if(potential < initialPotential)
			{
				//System.out.println("moved straight line");
				//best movement spot is straight to the node
				l = newL;
			}
			else
			{
				//System.out.println("attempting random configurations");
				/*
				 * the unit can potentially move more than once as long as the total movement
				 * is less than the movement amount of the unit
				 */
				int attempts = 20;
				double totalMovement = 0; //total amount moved
				for(int i = 0; i < attempts && totalMovement < movement; i++)
				{
					//double m = movement;
					double m = Math.random()*movement;
					if(m+totalMovement > movement)
					{
						m = movement-totalMovement;
					}
					
					v = rotateVector(Math.random()*360, v);
					
					newL = new double[]{l[0]+v[0]*m, l[1]+v[1]*m};
					potential = getPotential(new Circle(newL, radius), obstacles, units, target, pindex == path.size()-1);
					//System.out.println("potential="+p);
					if(potential < initialPotential)
					{
						//System.out.println("found lower potential config!");
						totalMovement+=m;
						l = newL;
					}
				}
			}
			if(potential == 0)
			{
				pindex++;
			}
		}
		else
		{
			path = null;
			isMoving = false;
		}
	}
	/**
	 * gets the potential for a given configuration, if the configuration is intersecting
	 * either a unit or obstacle its potential is the maximum value of a double
	 * 
	 * potential is zero if the passed circle to be evaluated is within the minimum distance
	 * to the target path point node
	 * 
	 * @param c
	 * @param obstacles
	 * @param units
	 * @param target
	 * @return
	 */
	private double getPotential(Circle c, Circle[] obstacles, Unit[] units, PointNode target, boolean finalNode)
	{
		double p = 0;
		for(int i = 0; i < obstacles.length; i++)
		{
			double distance = distance(c, obstacles[i]);
			if(distance < 200)
			{
				p+=1000./distance;
			}
			if(c.intersects(obstacles[i]))
			{
				return Double.MAX_VALUE;
			}
		}
		for(int i = 0; i < units.length; i++)
		{
			double distance = distance(c, units[i]);
			if(distance < 300 && units[i].priority > priority) //lower priority units yield to higher priority units
			{
				if(units[i].isMoving)
				{
					p+=6000/distance;
				}
				else
				{
					p+=1000/distance;
				}
			}
			if(units[i] != this && c.intersects(units[i]))
			{
				return Double.MAX_VALUE;
			}
		}
		double distance = distance(c.l, target.l);
		if(distance < minDist && !finalNode)
		{
			return 0;
		}
		else if(distance < finalDist && finalNode)
		{
			return 0;
		}
		p+=distance*distance;
		return p;
	}
	public void cancelAction()
	{
		System.out.println("move action canceled");
	}
	public void initiateAction(World w)
	{
		p.setPath(path);
	}
}
