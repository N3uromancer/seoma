package world.unit.action;

import geom.Boundable;

import java.util.HashSet;

import pathfinder.Pathfinder;
import pathfinder.localPlanner.LocalPlanner;
import pathfinder.path.ApproachType;
import utilities.MathUtil;
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
		LocalPlanner lp = pf.getLocalPlanner();
		double[] l = p.getLocation();
		
		double searchWidth = 60;
		double searchHeight = 60;
		
		HashSet<Boundable> b = w.getPartition(PathObstacle.class).intersects(l[0]-searchWidth/2, l[1]-searchHeight/2, searchWidth, searchHeight);
		HashSet<PathObstacle> obstacles = new HashSet<PathObstacle>();
		for(Boundable boundable: b)
		{
			obstacles.add((PathObstacle)boundable);
		}
		ApproachType approach = p.getPath().getPathNodeIndex()==p.getPath().getPath().keySet().size()-1? ApproachType.Arrval: ApproachType.None;
		double[] target = p.getPath().getPath().get(p.getPath().getPathNodeIndex()).l;
		lp.movePathableObj(p, target, approach, obstacles, tdiff);
		
		if(MathUtil.distance(target[0], target[1], p.getLocation()[0], p.getLocation()[1]) < 70 && approach == ApproachType.None)
		{
			p.getPath().incrementPathNodeIndex();
		}
		
		return p.getPath() == null;
		
		/*//System.out.println("performing move...");
		LocalPlanner lp = pf.getLocalPlanner();
		double[] l = p.getLocation();
		
		double searchWidth = 60;
		double searchHeight = 60;
		
		HashSet<Boundable> b = w.getPartition(PathObstacle.class).intersects(l[0]-searchWidth/2, l[1]-searchHeight/2, searchWidth, searchHeight);
		HashSet<PathObstacle> obstacles = new HashSet<PathObstacle>();
		for(Boundable boundable: b)
		{
			obstacles.add((PathObstacle)boundable);
		}
		//System.out.println("obstacles = "+obstacles.size());
		//System.out.println(w.getPartition(PathObstacle.class).size());
		lp.movePathableObj(p, target, ApproachType.Arrval, obstacles, tdiff);
		//System.out.println("move complete = "+(p.getPath()==null));
		//return p.getPath()==null;
		
		//return MathUtil.distance(target[0], target[1], p.getLocation()[0], p.getLocation()[1]) < 40;
		return p.getPath() == null;*/
	}
	public void cancelAction()
	{
		//System.out.println("move action canceled");
	}
	public void initiateAction(World w)
	{
		pf = w.getPathfinder();
		//System.out.println("move action initiated");
		HashSet<Pathable> temp = new HashSet<Pathable>();
		temp.add(p);
		pf.findPath(temp, target);
		
		//System.out.println("path found = "+(p.getPath()!=null));
		if(p.getPath()!=null)
		{
			//System.out.println(p.getPath());
		}
		else
		{
			//System.out.println("null path, canceling move action");
			cancelAction();
		}
	}
}
