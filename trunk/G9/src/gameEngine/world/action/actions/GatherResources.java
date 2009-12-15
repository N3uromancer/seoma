package gameEngine.world.action.actions;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;

import ai.AI;

import utilities.Location;
import utilities.MathUtil;

import gameEngine.world.World;
import gameEngine.world.action.Action;
import gameEngine.world.action.ActionList;
import gameEngine.world.owner.Owner;
import gameEngine.world.resource.ResourceDeposit;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.unitModifiers.Gatherer;
import gameEngine.world.unit.units.Refinery;

public class GatherResources extends ActionList
{
	Unit gatherer;
	ResourceDeposit target;
	World w;
	AI ai;
	
	public GatherResources(Unit gatherer, ResourceDeposit target, World w, AI ai)
	{
		super("gather resources");
		this.gatherer = gatherer;
		this.target = target;
		this.w = w;
		this.ai = ai;
	}
	public void startAction()
	{
		double[] s = gatherer.getLocation();
		//System.out.println("gatherer starting at "+s[0]+", "+s[1]);
		double[] t = target.getLocation();
		Location[] l = w.getPathFinder().determinePath(s[0], s[1], t[0], t[1]);
		addActionToList(new MoveList(gatherer, l));
		
		addActionToList(new LoadResources((Gatherer)gatherer, target, s));
		addActionToList(new MoveToRefinery(gatherer, ai, w));
		addActionToList(new DepositResources((Gatherer)gatherer, gatherer.getOwner()));
		super.startAction();
	}
}
/**
 * an action that continually moves a unit to a near refinery
 * @author Jack
 *
 */
class MoveToRefinery extends Action
{
	Unit u;
	AI ai;
	World w;
	
	Unit target = null; //the target refinery the unit is moving towards
	MoveList mover;
	
	public MoveToRefinery(Unit u, AI ai, World w)
	{
		super("move to refinery");
		this.u = u;
		this.ai = ai;
		this.w = w;
	}
	public void cancelAction(){}
	public void drawAction(GL gl){}
	public boolean performAction(double tdiff)
	{
		//target exists and is alive
		if(target != null && target.getLife() > 0)
		{
			//finishes when it gets to the refiner's position
			return mover.performAction(tdiff);
		}
		else
		{
			//searchs for another refiner to move towards
			startAction();
		}
		return false;
	}
	public void startAction()
	{
		target = getClosestRefiner(Refinery.class);
		
		if(target != null)
		{
			double[] s = u.getLocation();
			double[] t = target.getLocation();
			Location[] l = w.getPathFinder().determinePath(s[0], s[1], t[0], t[1]);
			mover = new MoveList(u, l);
		}
	}
	private Unit getClosestRefiner(Class<? extends Unit> c)
	{
		ArrayList<Unit> r = ai.getUnits().get(c);
		Unit closest = null;
		if(r != null && r.size() > 0)
		{
			//cycles through refiners and finds the closest
			Iterator<Unit> i = r.iterator();
			double distance = 0;
			while(i.hasNext())
			{
				Unit temp = i.next();
				double distance2 = MathUtil.distance(temp.getLocation()[0], temp.getLocation()[1], u.getLocation()[0], u.getLocation()[1]);
				if(closest == null || distance2 < distance)
				{
					distance = distance2;
					closest = temp;
				}
			}
		}
		return closest;
	}
}
class DepositResources extends Action
{
	Gatherer g;
	Owner o;
	
	public DepositResources(Gatherer g, Owner o)
	{
		super("deposit resource");
		this.g = g;
		this.o = o;
	}
	public void cancelAction(){}
	public void drawAction(GL gl){}
	public boolean performAction(double tdiff)
	{
		o.setResources(o.getResources()+g.getLoad());
		return true;
	}
	public void startAction(){}
}
/**
 * action for gathering resources over time from a deposit
 * @author Jack
 *
 */
class LoadResources extends Action
{
	Gatherer g;
	ResourceDeposit rd;
	double[] l;
	
	/**
	 * creates a new action to gather resources
	 * @param g
	 * @param rd
	 * @param l the location of the gatherer for drawing purposes
	 */
	public LoadResources(Gatherer g, ResourceDeposit rd, double[] l)
	{
		super("load resource");
		this.g = g;
		this.rd = rd;
		this.l = l;
	}
	public void cancelAction()
	{
		
	}
	public void drawAction(GL gl)
	{
		int lines = 8;
		double maxLength = 20;
		double d = -.1; //depth
		gl.glPushMatrix();
		gl.glColor4d(.7, .2, .9, .5);
		gl.glBegin(GL.GL_LINES);
		for(int i = 0; i < lines; i++)
		{
			double length = maxLength*Math.random();
			gl.glRotated(Math.random()*360, 0, 0, 1);
			gl.glVertex3d(0, 0, d);
			gl.glVertex3d(length, 0, d);
		}
		gl.glEnd();
		gl.glPopMatrix();
	}
	/**
	 * loads resources into the gatherer, finished when the gatherer
	 * is at its max load capacity or the resource deposit is empty
	 */
	public boolean performAction(double tdiff)
	{
		double loadAmount = g.getGatherRate()*tdiff;
		if(g.getLoad()+loadAmount > g.getMaxLoad())
		{
			loadAmount = g.getMaxLoad()-g.getLoad();
		}
		if(rd.getTotalResources() < loadAmount)
		{
			loadAmount = rd.getTotalResources();
		}
		rd.setTotalResources(rd.getTotalResources()-loadAmount);
		g.setLoad(g.getLoad()+loadAmount);
		
		//System.out.println("resource loading complete, rd total="+rd.getTotalResources());
		return g.getLoad()==g.getMaxLoad() || rd.getTotalResources()==0;
	}
	public void startAction()
	{
		
	}
}