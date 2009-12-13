package gameEngine.world.unit;

import gameEngine.StartSettings;
import gameEngine.world.World;
import gameEngine.world.owner.Owner;
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
	
	/**
	 * creates the unit engine
	 * @param width the width of the game world
	 * @param height the height of the game world
	 */
	public UnitEngine(StartSettings ss, World w)
	{
		ausp = new SpatialPartition(0, 0, ss.getMapWidth(), ss.getMapHeight(), 30, 50, 100);
		
		System.out.print("creating starting units... ");
		Owner[] o = ss.getOwners();
		int unitsCreated = 0;
		for(int i = 0; i < o.length; i++)
		{
			u.put(o[i], new LinkedList<Unit>());
			usp.put(o[i], new SpatialPartition(0, 0, ss.getMapWidth(), ss.getMapHeight(), 30, 50, 100));
			
			String[] sunits = ss.getStartingUnits();
			for(int a = 0; a < sunits.length; a++)
			{
				//Unit unit = UnitFactory.createUnit(sunits[a], o[i], Math.random()*ss.getMapWidth(), Math.random()*ss.getMapHeight());
				double x = w.getStartLocations().get(i).getLocation()[0];
				double y = w.getStartLocations().get(i).getLocation()[1];
				Unit unit = UnitFactory.createUnit(sunits[a], o[i], x, y);
				unit.getOwner();
				registerUnit(unit);
				unitsCreated++;
				/*for(int q = 0; q < 100; q++)
				{
					double x = m.getStartLocations().get(i).getLocation()[0];
					double y = m.getStartLocations().get(i).getLocation()[1];
					//Unit unit = UnitFactory.createUnit(sunits[a], o[i], Math.random()*ss.getMapWidth(), Math.random()*ss.getMapHeight());
					Unit unit = UnitFactory.createUnit(sunits[a], o[i], x, y);
					unit.getOwner();
					registerUnit(unit);
					unitsCreated++;
				}*/
			}
		}
		System.out.println("units created = "+unitsCreated);
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
	}
	/**
	 * updates game units, removes dead units from the game
	 * @param tdiff
	 * @param w
	 */
	public void updateUnitEngine(double tdiff, World w)
	{
		Iterator<Owner> i = u.keySet().iterator();
		while(i.hasNext())
		{
			Iterator<Unit> ui = u.get(i.next()).iterator();
			while(ui.hasNext())
			{
				Unit unit = ui.next();
				if(unit.isDead())
				{
					//usp.removeRegion(unit);
					usp.get(unit.getOwner()).removeRegion(unit);
					ausp.removeRegion(unit);
					ui.remove();
					//System.out.println("unit died");
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
	}
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
	public boolean isDead()
	{
		return false;
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
