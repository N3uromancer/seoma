package ai.computerAI.computerEasy;

import world.unit.unitModifiers.Gatherer;
import world.unit.unitModifiers.Refiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import ai.AI;
import utilities.MathUtil;
import world.World;
import world.action.actions.MoveList;
import world.resource.ResourceDeposit;
import world.unit.Building;
import world.unit.Unit;
import world.unit.unitModifiers.Builder;
import world.unit.units.DefenseTurret;
import world.unit.units.Engineer;
import world.unit.units.Factory;
import world.unit.units.Harvester;
import world.unit.units.Leader;
import world.unit.units.Refinery;
import world.unit.units.Tank;

/**
 * represents a viable expansion site, stores the buildings at the site,
 * records number of times units have died near the site
 * 
 * 
 * used to abstract overall team control, units are simply associated with an expansion
 * site, associated units are then said to be managed by the site and receive their
 * commands from the site
 * 
 * @author Secondary
 *
 */
public final class ExpansionSite
{
	/**
	 * a reference to the underlying ai class
	 */
	ComputerEasy ai;
	/**
	 * a reference to the game world
	 */
	World w;
	/**
	 * the resource deposit the expansion site is centered around
	 */
	ResourceDeposit rd;
	/**
	 * the area enclosed by the expansion site
	 */
	double radius;
	/**
	 * records the deaths of various units inside the expansion site
	 */
	HashMap<Class<? extends Unit>, Integer> deathCounts = new HashMap<Class<? extends Unit>, Integer>();
	/**
	 * the total number of deaths that occured within the expansion site
	 */
	int totalDeaths = 0;
	/**
	 * a set containing builder units that are associated with the expansion site
	 */
	HashSet<Unit> builders = new HashSet<Unit>();
	/**
	 * a set containing fighting units, units that have weapons
	 */
	HashSet<Unit> fighters = new HashSet<Unit>();
	/**
	 * units capable of gathering that are associated with the site
	 */
	HashSet<Unit> gatherers = new HashSet<Unit>();
	/**
	 * contains units capable of refining resources, unload stations for resources
	 */
	HashSet<Unit> refiners = new HashSet<Unit>();
	
	/**
	 * keeps track of the numbers of different units associated with the expansion site
	 */
	HashMap<Class<? extends Unit>, Integer> unitCounts = 
		new HashMap<Class<? extends Unit>, Integer>();
	
	boolean createdNewSite = false;
	
	/**
	 * creates a new expansion site centered at the passed resource deposit,
	 * orders the initial construction of a refinery at the expansion site
	 * @param rd
	 */
	public ExpansionSite(ResourceDeposit rd, ComputerEasy ai, World w)
	{
		this.rd = rd;
		radius = rd.getRadius()*1.3;
		this.ai = ai;
		this.w = w;
	}
	/**
	 * associates the passed unit with the expansion site, associated
	 * units then carry out build orders commanded of the site and defend
	 * the site from attack
	 * @param u
	 */
	public void associateUnit(Unit u)
	{
		if(unitCounts.containsKey(u.getClass()))
		{
			unitCounts.put(u.getClass(), unitCounts.get(u.getClass())+1);
		}
		else
		{
			unitCounts.put(u.getClass(), 1);
		}
		
		if(u instanceof Builder)
		{
			builders.add(u);
		}
		else if(u instanceof Gatherer)
		{
			gatherers.add(u);
		}
		else if(u.getWeapon() != null)
		{
			fighters.add(u);
		}
		else if(u instanceof Refiner)
		{
			refiners.add(u);
		}
	}
	public void disassociateUnit(Unit u)
	{
		unitCounts.put(u.getClass(), unitCounts.get(u.getClass())-1);
		builders.remove(u);
		gatherers.remove(u);
		fighters.remove(u);
		refiners.remove(u);
	}
	/**
	 * builds a refinery at the expansion site
	 * @param builder
	 */
	public void buildRefinery(Unit builder)
	{
		double[] v = {rd.getRadius(), 0};
		v = MathUtil.rotateVector(Math.random()*360, v);
		ai.moveUnit(builder, rd.getLocation()[0]+v[0], rd.getLocation()[1]+v[1], w);
		ai.buildUnit(Refinery.class, builder, w);
	}
	/**
	 * checks to see if the passed location resides inside the expansion site
	 * @param x
	 * @param y
	 * @return returns true if the passed point is contained within the expansion
	 * site radius, false otherwise
	 */
	public boolean contains(double x, double y)
	{
		return MathUtil.distance(x, y, rd.getLocation()[0], rd.getLocation()[1]) < radius;
	}
	/**
	 * records that a unit died within the expansion site bounds
	 * @param u
	 */
	public void logDeath(Unit u)
	{
		if(deathCounts.containsKey(u.getClass()))
		{
			deathCounts.put(u.getClass(), deathCounts.get(u.getClass())+1);
		}
		else
		{
			deathCounts.put(u.getClass(), 1);
		}
		totalDeaths++;
		
		builders.remove(u);
		fighters.remove(u);
		gatherers.remove(u);
		refiners.remove(u);
		unitCounts.put(u.getClass(), unitCounts.get(u.getClass())-1);
	}
	/**
	 * builds a unit at the expansion site using workers at the expansion site,
	 * if no unit that can build the building is deemed "free", as in not having
	 * other actions to complete, then a busy unit is told to complete the build
	 * task if the queue boolean is true
	 * @param c
	 * @param queue if true then busy builders that are capable of building the
	 * specified unit are told to build the unit after their other tasks have
	 * been completed
	 * @return returns true if a build order for the specified unit was placed,
	 * false otherwise, false will be returned if no associated builder can build
	 * the specified unit
	 */
	public boolean buildUnit(Class<? extends Unit> c, boolean queue)
	{
		boolean built = false;
		HashSet<Unit> busy = new HashSet<Unit>(); //contains builders that can build the unit but are busy
		for(Unit b: builders)
		{
			if(((Builder)b).canBuild(c))
			{
				if(b.getQueuedActions().size() == 0 || (b.getQueuedActions().size() == 1 && b.getQueuedActions().peek() instanceof MoveList))
				{
					//builder doing nothing or only moving
					b.clearActions();
					double[] v = {rd.getRadius(), 0};
					v = MathUtil.rotateVector(Math.random()*360+3, v);
					ai.moveUnit(b, rd.getLocation()[0]+v[0], rd.getLocation()[1]+v[1], w);
					built = ai.buildUnit(c, b, w);
					break;
				}
				else
				{
					//builder busy
					busy.add(b);
				}
			}
		}
		if(!built && busy.size() > 0)
		{
			for(Unit b: busy)
			{
				//orders a busy unit to build the building after it finishes its other tasks
				double[] v = {rd.getRadius(), 0};
				v = MathUtil.rotateVector(Math.random()*360+3, v);
				ai.moveUnit(b, rd.getLocation()[0]+v[0], rd.getLocation()[1]+v[1], w);
				built = ai.buildUnit(c, b, w);
				break;
			}
		}
		return built;
	}
	/**
	 * gives orders to the passed unit, the passed unit must be associated with this
	 * expansion site
	 * @param u
	 */
	public void manageUnit(Unit u)
	{
		if(u.getCurrentAction() == null)
		{
			if(gatherers.contains(u))
			{
				ai.gatherResources(u, rd, w);
			}
			else if(fighters.contains(u))
			{
				ai.moveUnitInRect(u, w, rd.getLocation(), radius*1.3, radius*1.3);
			}
			else if(u instanceof Leader)
			{
				ai.moveUnitInRect(u, w, rd.getLocation(), radius*.9, radius*.9);
			}
		}
	}
	/**
	 * gets the resource deposit this site is centered at
	 * @return
	 */
	public ResourceDeposit getDeposit()
	{
		return rd;
	}
	public void updateSite(ExpansionSiteManager esm)
	{
		if(gatherers.size() < 20)
		{
			buildUnit(Harvester.class, false);
			//System.out.println("harvester building = "+buildUnit(Harvester.class, false));
		}
		if(builders.size() < 3)
		{
			//buildUnit(Factory.class, false);
			//buildUnit(Factory.class, false);
			//buildUnit(Engineer.class, false);
		}
		if(fighters.size() < 20)
		{
			buildUnit(Tank.class, false);
		}
		if(refiners.size() == 0)
		{
			//buildUnit(Refinery.class, false);
		}
		if(rd.getTotalResources() < 300 && !createdNewSite)
		{
			createdNewSite = true;
			
			//System.out.println("creating new expansion site...");
			ResourceDeposit rd = ai.getClosestResourceDeposit(this.rd.getLocation(), 600, esm.getUsedDeposits(), w);
			ExpansionSite expo = new ExpansionSite(rd, ai, w);
			esm.addExpansionSite(expo);
			int count = 0;
			HashSet<Unit> transfer = new HashSet<Unit>();
			//System.out.println("giving gatherers...");
			Iterator<Unit> i = gatherers.iterator();
			while(i.hasNext() && count < 6)
			{
				Unit u = i.next();
				ai.moveUnit(u, rd.getLocation()[0], rd.getLocation()[1], w);
				//esm.disassociateUnit(u);
				//esm.associateUnit(u, expo);
				transfer.add(u);
				count++;
				//System.out.println(u+" association changed");
			}
			//System.out.println("done");
			count = 0;
			for(Unit u: builders)
			{
				if(!(u instanceof Building))
				{
					transfer.add(u);
					//esm.disassociateUnit(u);
					//esm.associateUnit(u, expo);
					count++;
					//System.out.println(u+" association changed");
					if(count > 2)
					{
						break;
					}
				}
			}
			count = 0;
			for(Unit u: fighters)
			{
				transfer.add(u);
				//esm.disassociateUnit(u);
				//esm.associateUnit(u, expo);
				count++;
				//System.out.println(u+" association changed");
				if(count > 10)
				{
					break;
				}
			}
			for(Unit u: transfer)
			{
				esm.disassociateUnit(u);
				esm.associateUnit(u, expo);
			}
			expo.buildUnit(Refinery.class, true);
			expo.buildUnit(Factory.class, true);
			//System.out.println("building new refinery = "+expo.buildUnit(Refinery.class, true));
			//System.out.println("building new factory = "+expo.buildUnit(Factory.class, true));
			/*expo.buildUnit(DefenseTurret.class, true);
			expo.buildUnit(DefenseTurret.class, true);
			expo.buildUnit(DefenseTurret.class, true);
			expo.buildUnit(DefenseTurret.class, true);*/
			//System.out.println("done");
			//System.out.println("-------------------------");
		}
	}
}
