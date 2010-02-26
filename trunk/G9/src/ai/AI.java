package ai;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.opengl.GL;

import ui.userIO.userInput.UserInput;
import utilities.Camera;
import utilities.MathUtil;
import world.World;
import world.action.actions.Build;
import world.action.actions.GatherResources;
import world.action.actions.MoveList;
import world.owner.Owner;
import world.resource.ResourceDeposit;
import world.unit.BuildOrder;
import world.unit.Unit;
import world.unit.unitModifiers.Builder;
import world.unit.unitModifiers.Gatherer;
import world.unit.units.Refinery;
import ai.aiModule.AIModule;

/**
 * defines the base methods for an ai, each ai also handles the user actions
 * of its owner (computer ais dont need to use these methods) including
 * mouse clicks, drags, etc
 * @author Jack
 *
 */
public abstract class AI
{
	protected Owner o;
	
	//keeps track of the different types of units under control of this ai
	HashMap<Class<? extends Unit>, ArrayList<Unit>> units = 
		new HashMap<Class<? extends Unit>, ArrayList<Unit>>();
	
	ArrayList<AIModule> m = new ArrayList<AIModule>();
	
	long updateTime = 0;
	long updates = 0;
	
	public AI(Owner o)
	{
		this.o = o;
	}
	public void registerAIModule(AIModule module)
	{
		m.add(module);
	}
	/**
	 * called by the unit engine to notify the ai that a unit has been constructed,
	 * stores the unit in the ai by type
	 * @param c the unit that was contructed
	 */
	public void unitConstructed(Unit u)
	{
		//System.out.println("unit constructed, types = "+units.keySet().size());
		if(units.get(u.getClass()) != null)
		{
			units.get(u.getClass()).add(u);
		}
		else
		{
			ArrayList<Unit> temp = new ArrayList<Unit>();
			temp.add(u);
			units.put(u.getClass(), temp);
		}
	}
	/**
	 * called by the unit engine to notiyfy the ai that a unit has been
	 * destroyed, the destroyed unit is then removed from the ai's list of units
	 * @param u
	 */
	public void unitDestroyed(Unit u)
	{
		units.get(u.getClass()).remove(u);
	}
	/**
	 * a convenience method for getting resource deposits on the map
	 * @param w
	 * @return returns a list of resource deposits
	 */
	public ArrayList<ResourceDeposit> getResourceDeposits(World w)
	{
		return w.getResourceEngine().getResourceDeposits();
	}
	/**
	 * orders the passed unit to gather resources form the specified deposit
	 * @param u the gatherer
	 * @param rd the resource deposit the unit is being ordered to gather from
	 * @param w
	 * @return returns true if the order was successfully added to the units
	 * action list, false otherwise
	 */
	public boolean gatherResources(Unit u, ResourceDeposit rd, World w)
	{
		if(u instanceof Gatherer && units.get(Refinery.class) != null && units.get(Refinery.class).size() > 0)
		{
			u.addAction(new GatherResources(u, rd, w, this));
			return true;
		}
		return false;
	}
	/**
	 * gets and compacts all enemy units into a hash map
	 * @param w
	 * @return returns a map representing all enemy units
	 */
	public HashMap<Owner, LinkedList<Unit>> getEnemyUnits(World w)
	{
		HashMap<Owner, LinkedList<Unit>> m = new HashMap<Owner, LinkedList<Unit>>();
		HashMap<Owner, LinkedList<Unit>> master = w.getUnitEngine().getUnitList();
		Iterator<Owner> i = master.keySet().iterator();
		while(i.hasNext())
		{
			Owner temp = i.next();
			if(temp != o)
			{
				m.put(temp, master.get(temp));
			}
		}
		return m;
	}
	/**
	 * called after the game has been created and when the ai is first run
	 * @param w a reference to the game world
	 */
	public abstract void initialize(World w);
	/**
	 * orders a unit to build another unit
	 * @param u
	 * @param builder
	 * @param w
	 * @return returns true if the build order was initiated, false otherwise
	 */
	public boolean buildUnit(Class<? extends Unit> u, Unit builder, World w)
	{
		if(builder instanceof Builder)
		{
			Builder b = (Builder)builder;
			//System.out.println(builder.getClass().getSimpleName()+" is a builder");
			Unit temp = null;
			try
			{
				Class<?>[] arguments = {Owner.class, double.class, double.class};
				Object[] parameters = {builder.getOwner(), 0, 0};
				Constructor<? extends Unit> c = u.getConstructor(arguments);
				temp = c.newInstance(parameters);
			}
			catch(Exception e){}
			if(b.canBuild(u) && builder.getOwner().getResources() >= temp.getCost())
			{
				//System.out.println("can build "+u.getSimpleName());
				builder.getOwner().setResources(builder.getOwner().getResources()-temp.getCost());
				BuildOrder bo = new BuildOrder(temp.getBuildTime(), u, builder);
				builder.addAction(new Build(bo, w));
				return true;
			}
		}
		return false;
	}
	/**
	 * gets the units controlled by this ai
	 * @return returns a list of all controlled units
	 */
	public LinkedList<Unit> getUnits(World w)
	{
		return w.getUnitEngine().getUnitList(o);
	}
	/**
	 * gets a hash map representing all units under the control of this ai
	 * sorted into types
	 * @return
	 */
	public HashMap<Class<? extends Unit>, ArrayList<Unit>> getUnits()
	{
		return units;
	}
	/**
	 * returns the camera specifying the view of this ai for
	 * drawing purposes
	 * @return
	 */
	public abstract Camera getCamera();
	/**
	 * orders the passed unit to move to the passed game location,
	 * pushes a move order to the unit action stack
	 * @param u the unit being moved, its movement must be greater than zero
	 * @param x the x position the unit is being told to move to, must be in the map
	 * @param y the y position the unit is being told to move to, must be in the map
	 */
	public void moveUnit(Unit u, double x, double y, World w)
	{
		if(w.inWorld(x, y) && u.getMovement() > 0)
		{
			u.addAction(new MoveList(u, w, new double[]{x, y}));
		}
	}
	/**
	 * calls the method to perform the ai functions and updates all registered
	 * modules for the ai, called to update the ai
	 * @param w
	 * @param ui
	 * @param tdiff
	 */
	public void performAIFunctions(World w, HashMap<Class<? extends UserInput>, ArrayList<UserInput>> ui, double tdiff)
	{
		long start = System.currentTimeMillis();
		if(m.size() > 0)
		{
			Iterator<AIModule> i = m.iterator();
			while(i.hasNext())
			{
				i.next().updateModule(this, w, ui, tdiff);
			}
		}
		performAIFunctions(w, ui);
		updateTime += System.currentTimeMillis()-start;
		updates++;
		if(updates != 0 && updates%1000 == 0)
		{
			System.out.println(o.getName()+" ai average update time (ms) = "+updateTime+" [total time] / "+updates+" [updates] = "+(updateTime*1.0/updates));
			System.out.println("------");
		}
	}
	/**
	 * the main AI method, performs the ai functions
	 * of the main thread
	 * @param w
	 * @param ui user input performed by this ai's owner
	 */
	protected abstract void performAIFunctions(World w, HashMap<Class<? extends UserInput>, ArrayList<UserInput>> ui);
	/**
	 * draws a UI for this ai
	 * @param gl
	 */
	public abstract void drawUI(GL gl);
}
