package ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import gameEngine.world.World;
import gameEngine.world.action.actions.*;
import gameEngine.world.owner.Owner;
import gameEngine.world.resource.ResourceDeposit;
import gameEngine.world.unit.BuildOrder;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.unitModifiers.*;
import gameEngine.world.unit.units.Refinery;

import javax.media.opengl.GL;
import ui.userIO.userInput.UserInput;
import utilities.Camera;
import utilities.Location;

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
	
	public AI(Owner o)
	{
		this.o = o;
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
	public void unitDestroy(Unit u)
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
			if(b.canBuild(u))
			{
				//System.out.println("can build "+u.getSimpleName());
				BuildOrder bo = new BuildOrder(u, builder);
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
			//System.out.println("unit ordered to move to ("+x+", "+y+")");
			double[] s = u.getLocation();
			Location[] l = w.getPathFinder().determinePath(s[0], s[1], x, y);
			//u.addAction(new Move(u, x, y));
			/*for(int i = 0; i < l.length; i++)
			{
				u.addAction(new Move(u, l[i].x, l[i].y));
			}*/
			u.addAction(new MoveList(u, l));
		}
	}
	/**
	 * the main AI method, called once by the SGEngine every iteration
	 * of the main thread
	 * @param w
	 * @param ui user input performed by this ai's owner
	 */
	public abstract void performAIFunctions(World w, ArrayList<UserInput> ui);
	/**
	 * draws a UI for this ai
	 * @param gl
	 */
	public abstract void drawUI(GL gl);
}