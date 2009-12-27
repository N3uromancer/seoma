package gameEngine.world.unit;

import gameEngine.world.World;
import gameEngine.world.animation.animations.Explosion;
import gameEngine.world.owner.Owner;
import gameEngine.world.unit.units.Leader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import javax.media.opengl.GL;
import utilities.SpatialPartition;
import utilities.region.Region;

/**
 * manages all the units, culls dead units, moves units, fires unit weapons
 * @author Jack
 *
 */
public class UnitEngine
{
	SpatialPartition ausp; //all units spatial partition, for drawing
	HashMap<Owner, SpatialPartition> usp = new HashMap<Owner, SpatialPartition>(); //unit spatial partitions
	HashMap<Owner, LinkedList<Unit>> u = new HashMap<Owner, LinkedList<Unit>>(); //units
	
	LinkedList<BuildOrder> bo = new LinkedList<BuildOrder>();
	
	World w;
	
	long updates = 0;
	long totalTime = 0;
	long unitsUpdated = 0;
	
	/**
	 * creates the unit engine
	 * @param width the width of the game world
	 * @param height the height of the game world
	 */
	public UnitEngine(World w)
	{
		ausp = new SpatialPartition(0, 0, w.getMapWidth(), w.getMapHeight(), 30, 50, 100);
		this.w = w;
		
		System.out.print("creating starting units... ");
		Owner[] o = w.getOwners();
		for(int i = 0; i < o.length; i++)
		{
			u.put(o[i], new LinkedList<Unit>());
			usp.put(o[i], new SpatialPartition(0, 0, w.getMapWidth(), w.getMapHeight(), 30, 50, 100));
			
			double x = w.getStartLocations().get(i).getLocation()[0];
			double y = w.getStartLocations().get(i).getLocation()[1];
			registerUnit(new Leader(o[i], x, y));
		}
		System.out.println("done");
	}
	public HashMap<Owner, SpatialPartition> getUnits()
	{
		return usp;
	}
	/**
	 * adds units to the game, registers them with the unit spatial partition
	 * and with the linked list
	 * @param unit
	 */
	public void registerUnit(Unit unit)
	{
		//usp.addRegion(unit);
		usp.get(unit.getOwner()).addRegion(unit);
		ausp.addRegion(unit);
		u.get(unit.getOwner()).add(unit);
		
		w.getAIs().get(unit.getOwner()).unitConstructed(unit);
	}
	/**
	 * updates game units, removes dead units from the game
	 * @param tdiff
	 * @param w
	 */
	public void updateUnitEngine(double tdiff, World w)
	{
		long start = System.currentTimeMillis();
		Iterator<Owner> i = u.keySet().iterator();
		while(i.hasNext())
		{
			Iterator<Unit> ui = u.get(i.next()).iterator();
			while(ui.hasNext())
			{
				Unit unit = ui.next();
				unitsUpdated++;
				if(unit.isDead())
				{
					//usp.removeRegion(unit);
					usp.get(unit.getOwner()).removeRegion(unit);
					ausp.removeRegion(unit);
					ui.remove();
					w.getAIs().get(unit.getOwner()).unitDestroy(unit);
					//System.out.println("unit died");
					w.getAnimationEngine().registerAnimation(new Explosion(unit));
				}
				else
				{
					if(unit.getMovement() > 0)
					{
						usp.get(unit.getOwner()).removeRegion(unit);
						ausp.removeRegion(unit);
					}
					unit.update(w, tdiff);
					if(unit.getMovement() > 0)
					{
						usp.get(unit.getOwner()).addRegion(unit);
						ausp.addRegion(unit);
					}
				}
			}
		}
		//updates and removes all build orders
		Iterator<BuildOrder> bi = bo.iterator(); //build iterator
		while(bi.hasNext())
		{
			if(bi.next().update(tdiff, this))
			{
				bi.remove();
			}
		}
		
		totalTime+=System.currentTimeMillis()-start;
		updates++;
		if(updates % 1500 == 0 && unitsUpdated != 0)
		{
			System.out.println("unit engine update time (ms) = "+totalTime+" [total time] / "+updates+" [updates] = "+(totalTime/updates));
			System.out.println("unit update time (ms) = "+totalTime+" [total time] / "+unitsUpdated+" [units updated] = "+(totalTime/unitsUpdated));
			System.out.println("-------------------");
		}
	}
	/**
	 * gets a list of units for every owner
	 * @return returns a master list of all units sorted my owner into a map
	 */
	public HashMap<Owner, LinkedList<Unit>> getUnitList()
	{
		return u;
	}
	/**
	 * gets the list of units under the control of the passed owner
	 * @param o
	 * @return
	 */
	public LinkedList<Unit> getUnitList(Owner o)
	{
		return u.get(o);
	}
	/**
	 * gets a specific spatial partition
	 * @param o the owner of the units
	 * @return returns the specific spatial partition containing units
	 * whoose owner is the same as the passed owner
	 */
	public SpatialPartition getUnitPartition(Owner o)
	{
		return usp.get(o);
	}
	public void registerBuildOrder(BuildOrder order)
	{
		bo.add(order);
	}
	/**
	 * returns a single spatial partition representing all game units, used
	 * for determining which units are drawn and which are hit by explosions
	 * (because explosions hit both friendly and enemy units)
	 * @return
	 */
	public SpatialPartition getAllUnits()
	{
		return ausp;
	}
	public void drawAll(double x, double y, double width, double height, GL gl)
	{
		HashSet<Region> r = ausp.getIntersections(x, y, width, height);
		Iterator<Region> i = r.iterator();
		while(i.hasNext())
		{
			Unit u = (Unit)i.next();
			u.draw(gl);
		}
	}
}
