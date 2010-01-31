package world.weapon;

import geom.Boundable;
import geomUtil.PartitionManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import utilities.MathUtil;
import world.owner.Owner;
import world.shot.Shot;
import world.shot.ShotEngine;
import world.unit.Unit;

public abstract class Weapon
{
	/**
	 * the list of units in range of the weapon, re-determined each time
	 * the list length is zero
	 */
	LinkedList<Boundable> targetCache = new LinkedList<Boundable>();
	private double range;
	
	private double reloadTime = .5; //seconds
	private double timeSpent = 0; //the time that the gun has been reloading so far
	boolean fired = false;
	
	boolean calcCache = true; //if true then the target cache can be recalculated, alternates
	
	public Weapon(double range)
	{
		this.range = range;
	}
	public void updateWeapon(double tdiff)
	{
		if(fired)
		{
			timeSpent+=tdiff;
			if(timeSpent >= reloadTime)
			{
				fired = false;
				timeSpent = 0;
			}
		}
	}
	/**
	 * fires the weapon, registers a shot with the shot engine
	 * @param l the position of the center of the weapon
	 * @param o the owner of the weapon
	 * @param units the spatial partitions representing all units
	 */
	public void fireWeapon(Owner o, double[] l, HashMap<Owner, PartitionManager> units, ShotEngine se)
	{
		if(!fired)
		{
			if(targetCache.size() == 0 && calcCache)
			{
				//targetCache = new LinkedList<Region>(units.getRegions(x, y, range));
				targetCache = determineTargets(o, l[0], l[1], units);
			}
			calcCache = !calcCache;
			Unit u = null;
			if(targetCache.size() > 0)
			{
				Iterator<Boundable> i = targetCache.iterator();
				boolean unitFound = false;
				while(i.hasNext() && !unitFound)
				{
					u = (Unit)i.next();
					if(u.isDead() || MathUtil.distance(l[0], l[1], u.getLocation()[0], u.getLocation()[1]) > range)
					{
						i.remove();
						u = null;
					}
					else
					{
						unitFound = true;
					}
				}
				if(u == null)
				{
					//all units were removed from the cache
					targetCache = determineTargets(o, l[0], l[1], units);
					if(targetCache.size() != 0)
					{
						u = (Unit)targetCache.getFirst();
					}
				}
			}
			if(u != null)
			{
				se.registerShot(getShot(l[0], l[1], u, o));
				fired = true;
			}
		}
	}
	/**
	 * determines all targets in range, cycles through the spatial partitions of
	 * units not belonging to the owner of the weapon to find targets
	 * @param o the owner of the weapon
	 * @param x the x position of the center of the weapon
	 * @param y
	 * @param units the spatial partitions representing all game units
	 * @return returns a list of target units in range of the weapon
	 */
	private LinkedList<Boundable> determineTargets(Owner o, double x, double y, HashMap<Owner, PartitionManager> units)
	{
		LinkedList<Boundable> targetCache = new LinkedList<Boundable>();
		Iterator<Owner> oi = units.keySet().iterator();
		while(oi.hasNext())
		{
			Owner owner = oi.next();
			if(owner != o) //when owner==o, units are friendly
			{
				//targetCache.addAll(units.get(owner).getRegions(x, y, range));
				//2^(1/2) = 1.41421356, to save calls to expensive Math.sqrt method
				//double sq2 = 41421356;
				//double r = range*sq2;
				targetCache.addAll(units.get(owner).intersects(x-range, y-range, range*2, range*2));
			}
		}
		return targetCache;
	}
	/**
	 * gets the shot that the weapon fires, essentially this method creates the shot
	 * that is to be registered with the shot engine when the weapon is fired
	 * @param x the location of the weapon when it is fired, the starting position
	 * of the shot when fired
	 * @param y
	 * @param target the target that the shot was fired at
	 * @param o the owner of the shot, used so no shot hits its own team
	 * @return returns the shot fired by this weapon
	 */
	protected abstract Shot getShot(double x, double y, Unit target, Owner o);
}
