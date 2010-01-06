package ai.computerAI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.media.opengl.GL;

import ui.userIO.userInput.UserInput;
import utilities.Camera;
import utilities.MathUtil;
import gameEngine.world.World;
import gameEngine.world.owner.Owner;
import gameEngine.world.resource.ResourceDeposit;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.units.Factory;
import gameEngine.world.unit.units.Harvester;
import gameEngine.world.unit.units.Leader;
import gameEngine.world.unit.units.Refinery;
import gameEngine.world.unit.units.Tank;
import ai.AI;

public class StalinArmy extends AI {
	final static boolean dEnabled = true;
	Random rand = null;
	Owner me;
	int usableTankCount = 0;
	ArrayList<UnitGroup> swarmGroups;
	
	public StalinArmy(Owner o) {
		super(o);
		println("Initialized");
		swarmGroups = new ArrayList<UnitGroup>();
		uHash = new HashMap<Unit, UnitContext>();
		me = o;
	}
	
	private void println(String s)
	{
		if (dEnabled)
			System.out.println("StalinArmy: "+s);
	}

	public void drawUI(GL gl) {}
	public Camera getCamera() { return null; }

	int factoryMax = 2;
	int refineryMax = 1;
	int harvesterMax = 10;
	int tankMax = 50;
	int turretMax = 4;
	
	int swarmSize = 12;
	int maxDist = 50;
	
	double[] leaderPos;
	
	HashMap<Unit, UnitContext> uHash;
	
	protected void performAIFunctions(World w,
			HashMap<Class<? extends UserInput>, ArrayList<UserInput>> ui)
	{
		if (rand == null)
			rand = new Random(w.getSeed());
		
		LinkedList<Unit> units = getUnits(w);
		Iterator<Unit> i = units.iterator();
		while(i.hasNext())
		{
			Unit u = i.next();
			boolean ret = false;
			
			//This REALLY could be optimized
			if (uHash.get(u) == null)
				uHash.put(u, new UnitContext(u));
			
			if (u.getCurrentAction() != null) continue;
			
			if (u instanceof Leader)
			{
				if (getUnitCount(Factory.class) < factoryMax)
				{
					println("Factory count: "+getUnitCount(Factory.class));
					ret = buildUnit(Factory.class, u, w);
					println("Building factory: " + (ret ? "Success" : "Failed"));
				}
				if (getUnitCount(Refinery.class) < refineryMax && !ret)
				{
					println("Refinery count: "+getUnitCount(Refinery.class));
					ret = buildUnit(Refinery.class, u, w);
					println("Building refinery: " + (ret ? "Success" : "Failed"));
				}
				if (leaderPos == null)
					leaderPos = u.getLocation();
			}
			if (u instanceof Factory)
			{
				if (getUnitCount(Harvester.class) < harvesterMax)
				{
					println("Harvester count: "+getUnitCount(Harvester.class));
					ret = buildUnit(Harvester.class, u, w);
					println("Building harvester: " + (ret ? "Success" : "Failed"));
				}
				if (getUnitCount(Tank.class) < tankMax && !ret)
				{
					println("Tank count: "+getUnitCount(Tank.class));
					ret = buildUnit(Tank.class, u, w);
					println("Building tank: " + (ret ? "Success" : "Failed"));
					if (ret)
						usableTankCount++;
				}
				if (!ret)
				{
					println("Detected resource shortage (Factory)");
					harvesterMax++;
					return;
				}
			}
			if (u instanceof Harvester)
			{
				//println("Harvester gathering resources");
				gatherResources(u, getClosestResourceDeposit(u.getLocation(), w), w);
			}
			if (u instanceof Tank)
			{
				if (getUnitCount(Factory.class) != 0 && getUnits().get(Factory.class).get(0).getLife() < getUnits().get(Factory.class).get(0).getMaxLife())
				{
					println("Moving to save base");
					uHash.get(u).swarmU = null;
					uHash.get(u).swarmGroup = null;
					moveUnit(u, getUnits().get(Factory.class).get(0).getLocation()[0], getUnits().get(Factory.class).get(0).getLocation()[1], w);
				}
				else if (uHash.get(u).swarmU != null)
				{
					if (uHash.get(u).swarmU.isDead())
					{
						usableTankCount++;
						uHash.get(u).swarmU = null;
						println("Released swarming unit");
						println("Usable tank count: "+usableTankCount);
					}
				} else {
					/* FIXME: Find nearest base to guard */
					moveUnitRandomlyAroundArea(u, w, leaderPos, maxDist);
				}
			}
		}
		
		for (UnitGroup ug : swarmGroups)
		{
			boolean groupDead = true;
			for (Unit ugu : ug.u)
			{
				if (!ugu.isDead())
					groupDead = false;
			}
			if (groupDead && uHash.get(ug.u.get(0)).swarmGroup != null)
			{
				for (Unit ugu : ug.u)
				{
					uHash.get(ugu).swarmU = null;
					uHash.get(ugu).swarmGroup = null;
					System.out.println("Released dead swarm");
				}
				swarmSize += 5;
				tankMax += 10;
				println("swarm size: "+swarmSize+" tank max: "+tankMax);
			}
		}
		
		i = units.iterator();
		
		if (usableTankCount >= swarmSize)
		{
			ArrayList<Unit> sw = new ArrayList<Unit>();
			UnitGroup swarm = new UnitGroup(sw);
			while (i.hasNext())
			{
				Unit un = i.next();
				if (un instanceof Tank && uHash.get(un).swarmU == null)
				{
					double[][] rect = getEnemyRect(getEnemyOwners(w)[0], w);
					moveUnitRandomlyAroundRect(un, w, rect[0], rect[1][0], rect[1][1]);
					//FIXME: OMG HAX!!!!!!!!!!!!
					uHash.get(un).swarmU = un;
					
					uHash.get(un).swarmGroup = swarm;
					swarm.u.add(un);
					println("Tank swarming to "+rect[0][0]+", "+rect[0][1]);
					usableTankCount--;
					println("Usable tank count: "+usableTankCount);
				}
			}
			swarmGroups.add(swarm);
			println("Swarming group: "+swarmGroups.size());
		}
	}
		
		
	private Unit getEnemyUnit(World w)
	{
		double lowestHealth = Integer.MAX_VALUE;
		Unit lowestUnit = null;
		for (Owner o : w.getOwners())
		{
			if (o != me)
			{
				LinkedList<Unit> units = getEnemyUnits(w).get(o);
				for (Unit un : units)
				{
					if (!un.isDead() && un.getLife() < lowestHealth)
					{
						lowestHealth = un.getLife();
						lowestUnit = un;
					}
				}
			}
		}
		
		return lowestUnit;
	}
	
	private ResourceDeposit getClosestResourceDeposit(double[] p, World w)
	{
		ArrayList<ResourceDeposit> deposits = getResourceDeposits(w);
		int closest = -1;
		double dist = Double.MAX_VALUE;
		for(int i = 0; i < deposits.size(); i++)
		{
			ResourceDeposit rd = deposits.get(i);
			double thisDist = MathUtil.distance(p[0], p[1], rd.getLocation()[0], rd.getLocation()[1]);
			if(thisDist < dist && rd.getTotalResources() > 50)
			{
				closest = i;
				dist = thisDist;
			}
		}
		return deposits.get(closest);
	}
	public Owner[] getEnemyOwners(World w)
	{
		int i = 0;
		Owner[] ret = new Owner[w.getOwners().length-1];
		for (Owner o : w.getOwners())
		{
			if (o != me)
			{
				ret[i++] = o;
			}
		}
		return ret;
	}
	public void moveUnitRandomly(Unit u, World w)
	{
		moveUnit(u, rand.nextDouble()*w.getMapWidth(), rand.nextDouble()*w.getMapHeight(), w);
	}
	public void moveUnitRandomlyAroundArea(Unit u, World w, double[] p, int bounds)
	{
			moveUnit(u, p[0]-bounds+rand.nextDouble()*bounds*2, p[1]-bounds+rand.nextDouble()*bounds*2, w);
	}
	public void moveUnitRandomlyAroundRect(Unit u, World w, double[] p, double xbound, double ybound)
	{
			moveUnit(u, p[0]-xbound+rand.nextDouble()*xbound*2, p[1]-ybound+rand.nextDouble()*ybound*2, w);
	}
	//Returns 2 arrays of 2 points which represent the upper-left most point and the lower-right most point
	public  double[][] getEnemyRect(Owner o, World w)
	{
		double[][] ret = new double[][] {{Double.MAX_VALUE, Double.MAX_VALUE}, {0, 0}};
		
		LinkedList<Unit> units = getEnemyUnits(w).get(o);
		for (Unit u : units)
		{
			if (u.getLocation()[0] < ret[0][0])
				ret[0][0] = u.getLocation()[0];
			
			if (u.getLocation()[1] < ret[0][1])
				ret[0][1] = u.getLocation()[1];
		}
		
		for (Unit u : units)
		{
			if (u.getLocation()[0] - ret[0][0] > ret[1][0])
				ret[1][0] = u.getLocation()[0] - ret[0][0];
			
			if (u.getLocation()[1] - ret[0][1] > ret[1][0])
				ret[1][0] = u.getLocation()[1] - ret[0][1];
		}
		
		return ret;
	}
	private int getUnitCount(Class c)
	{
		int i = 0;
	
		//Easy case
		if (getUnits().get(c) == null ||
			getUnits().get(c).size() == 0)
			return 0;
		
		for (Unit u : getUnits().get(c))
			if (!u.isDead())
				i++;

		return i;
	}
}

class UnitContext {
	public Unit u;
	public Unit swarmU;
	public UnitGroup swarmGroup;
	
	public UnitContext(Unit u)
	{
		this.u = u;
		this.swarmU = null;
		this.swarmGroup = null;
	}
}

class UnitGroup {
	public ArrayList<Unit> u;
	
	public UnitGroup(ArrayList<Unit> u)
	{
		this.u = u;
	}
}
