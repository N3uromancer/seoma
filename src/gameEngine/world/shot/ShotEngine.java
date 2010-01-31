package gameEngine.world.shot;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import gameEngine.world.World;
import gameEngine.world.animation.animations.Explosion;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.UnitEngine;
import utilities.Polygon;
import utilities.SpatialPartition;
import utilities.region.Region;

/**
 * manages all shots, stores shots, updates shots, culls shots, determines
 * if a shot hits a unit
 * @author Jack
 *
 */
public final class ShotEngine
{
	SpatialPartition ssp; //shot spatial partition, stored as such for drawing purposes
	LinkedList<Shot> s = new LinkedList<Shot>();
	SpatialPartition psp; //polygon spatial partition

	long updates = 0;
	long totalTime = 0;
	long polygonIntersectionTime = 0;
	long unitIntersectionTime = 0;
	long shots = 0; //the number of shots the for which the intersections have been checked for
	
	World w;
	
	public ShotEngine(World w, Polygon[] p)
	{
		ssp = new SpatialPartition(0, 0, w.getMapWidth(), w.getMapHeight(), 20, 60, 100);
		psp = new SpatialPartition(0, 0, w.getMapWidth(), w.getMapHeight(), 10, 20, 100);
		for(int i = 0; i < p.length; i++)
		{
			psp.addRegion(p[i]);
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
			ssp.removeRegion(shot);
			shot.updateShot(tdiff);
			ssp.addRegion(shot);
			
			long ustart = System.currentTimeMillis();
			if(!shot.isDead())
			{
				HashSet<Region> intersections = ue.getAllUnits().getIntersections(shot);
				Iterator<Region> ri = intersections.iterator();
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
				HashSet<Region> intersections = psp.getIntersections(shot);
				Iterator<Region> ri = intersections.iterator();
				while(ri.hasNext() && !shot.isDead())
				{
					//System.out.println("here");
					Polygon p = (Polygon)ri.next();
					//shot.setDead();
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
				//System.out.println("shot dead");
				ssp.removeRegion(shot);
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
	public SpatialPartition getShotPartition()
	{
		return ssp;
	}
	public void registerShot(Shot shot)
	{
		//System.out.println("shot fired");
		ssp.addRegion(shot);
		s.add(shot);
	}
	public SpatialPartition getShots()
	{
		return ssp;
	}
}
