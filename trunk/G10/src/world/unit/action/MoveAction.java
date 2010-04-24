package world.unit.action;

import geom.Boundable;

import java.util.HashSet;

import pathfinder.Pathfinder;
import pathfinder.localPlanner.LocalPlanner;
import world.World;
import world.modifier.PathObstacle;
import world.modifier.Pathable;

/**
 * an action primitive that moves a unit
 * @author Secondary
 *
 */
public final class MoveAction implements Action
{
	private Pathable p;
	private double[] target;
	private Pathfinder pf;
	
	public MoveAction(Pathable p, double[] target)
	{
		this.p = p;
		this.target = target;
	}
	public boolean performAction(World w, double tdiff)
	{
		//System.out.println("performing move...");
		LocalPlanner lp = pf.getLocalPlanner();
		double[] l = p.getLocation();
		HashSet<Boundable> b = w.getPartition(PathObstacle.class).intersects(l[0]-100, l[1]-100, 200, 200);
		HashSet<PathObstacle> obstacles = new HashSet<PathObstacle>();
		for(Boundable boundable: b)
		{
			obstacles.add((PathObstacle)boundable);
		}
		lp.movePathableObj(p, target, obstacles, tdiff);
		//System.out.println("move complete = "+(p.getPath()==null));
		//return p.getPath()==null;
		return false;
	}
	public void cancelAction()
	{
		System.out.println("move action canceled");
	}
	public void initiateAction(World w)
	{
		pf = w.getPathfinder();
		//System.out.println("move action initiated");
		HashSet<Pathable> temp = new HashSet<Pathable>();
		temp.add(p);
		pf.findPath(temp, target);
		
		//System.out.println("path found = "+(p.getPath()!=null));
	}
}
