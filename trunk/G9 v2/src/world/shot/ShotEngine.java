package world.shot;

import geom.Boundable;
import geomUtil.PartitionManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import utilities.Polygon;
import utilities.SpatialPartition;
import utilities.region.Region;
import world.World;
import world.animation.animations.Explosion;
import world.unit.Unit;
import world.unit.UnitEngine;

/**
 * manages all shots, stores shots, updates shots, culls shots, determines
 * if a shot hits a unit
 * @author Jack
 *
 */
public final class ShotEngine
{
	PartitionManager ssp; //shot spatial partition, stored as such for drawing purposes
	LinkedList<Shot> s = new LinkedList<Shot>();
	PartitionManager psp; //polygon spatial partition

	long updates = 0;
	long totalTime = 0;
	long polygonIntersectionTime = 0;
	long unitIntersectionTime = 0;
	long shots = 0; //the number of shots the for which the intersections have been checked for
	
	World w;
	
	public ShotEngine(World w, Polygon[] p)
	{
		ssp = new PartitionManager(0, 0, w.getMapWidth(), w.getMapHeight(), 20, 60, 100);
		psp = new PartitionManager(0, 0, w.getMapWidth(), w.getMapHeight(), 10, 20, 100);
		for(int i = 0; i < p.length; i++)
		{
			psp.add(p[i]);
		}
		this.w = w;
	}
	public void updateShotEngine(double tdiff, UnitEngine ue)
	{
		updates++;
		long start = System.currentTimeMillis();
		
		Iterator<Shot> i = s.iterator();
		while(i.hasNext())
		{
			shots++;
			
			Shot shot = i.next();
			ssp.remove(shot);
			shot.updateShot(tdiff);
			ssp.add(shot);
			
			long ustart = System.currentTimeMillis();
			if(!shot.isDead())
			{
				HashSet<Boundable> intersections = ue.getAllUnits().intersects(shot);
				Iterator<Boundable> ri = intersections.iterator();
				while(ri.hasNext() && !shot.isDead())
				{
					Unit temp = (Unit)ri.next();
					if(temp.getOwner() != shot.getOwner())
					{
						temp.setLife(temp.getLife()-shot.getDamage());
						shot.setDead();
						
						double[] s = shot.getLocation();
						w.getAnimationEngine().registerAnimation(new Explosion(s[0], s[1], 5, 5, temp.getOwner().getColor(), 1, 2));
					}
				}
			}
			unitIntersectionTime+=System.currentTimeMillis()-ustart;
			
			long pstart = System.currentTimeMillis();
			if(!shot.isDead())
			{
				HashSet<Boundable> intersections = psp.intersects(shot);
				Iterator<Boundable> ri = intersections.iterator();
				while(ri.hasNext() && !shot.isDead())
				{
					Polygon p = (Polygon)ri.next();
					if(p.contains(shot.getLocation()[0], shot.getLocation()[1]))
					{
						shot.setDead();
						
						double[] s = shot.getLocation();
						w.getAnimationEngine().registerAnimation(new Explosion(s[0], s[1], 4, 4, null, 0, 3));
					}
				}
			}
			polygonIntersectionTime+=System.currentTimeMillis()-pstart;
			
			if(shot.isDead())
			{
				ssp.remove(shot);
				i.remove();
			}
		}
		
		totalTime+=System.currentTimeMillis()-start;
		
		if(updates % 1500 == 0 && shots != 0)
		{
			System.out.println("shot engine update time (ms) = "+totalTime+" [total time] / "+updates+" [updates] = "+(totalTime/updates));
			System.out.println("current shot count = "+s.size());
			System.out.println("unit/shot intersection time (ms) = "+unitIntersectionTime+" [total time] / "+shots+" [shots updated] = "+(unitIntersectionTime/shots));
			System.out.println("polygon/shot intersection time (ms) = "+polygonIntersectionTime+" [total time] / "+shots+" [shots updated] = "+(polygonIntersectionTime/shots));
			System.out.println("--------------");
		}
	}
	public void registerShot(Shot shot)
	{
		//System.out.println("shot fired");
		ssp.add(shot);
		s.add(shot);
	}
	public PartitionManager getShots()
	{
		return ssp;
	}
}
