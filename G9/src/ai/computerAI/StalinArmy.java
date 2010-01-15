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
import gameEngine.world.unit.units.DefenseTurret;
import gameEngine.world.unit.units.Engineer;
import gameEngine.world.unit.units.Factory;
import gameEngine.world.unit.units.Harvester;
import gameEngine.world.unit.units.Leader;
import gameEngine.world.unit.units.Refinery;
import gameEngine.world.unit.units.Tank;
import ai.AI;

public class StalinArmy extends AI {
	final static boolean dEnabled = false;
	Random rand = null;
	Owner me;
	ArrayList<UnitGroup> swarmGroups;
	HashMap<ResourceDeposit, ResourceContext> rHash;
	//HashMap<Class, Long> resourceLimits;
	int productionMax = 1;
	int turretsPerBase = 10;
	int minHarvester = 20;
	
	int tanksPerBase = 30;
	int minSwarmSize = 200;
	
	int factoryMultiplier = 2;
	
	/* Set to 5 to disable rect monitor */
	int currSet = 5;
	
	final static int borderMod = 75;
	
	//long swarmTimeout = 120000;
	
	int lastTankCount = 0;
	
	HashMap<Unit, UnitContext> uHash;
	
	public StalinArmy(Owner o) {
		super(o);
		println("Initialized");
		swarmGroups = new ArrayList<UnitGroup>();
		uHash = new HashMap<Unit, UnitContext>();
		rHash = new HashMap<ResourceDeposit, ResourceContext>();
		//resourceLimits = new HashMap<Class, Long>();
		//initializeResourceLimits();
		me = o;
	}
	
	/*private void initializeResourceLimits()
	{
		resourceLimits.put(Harvester.class, new Long(10));
		resourceLimits.put(Tank.class, new Long(10));
		resourceLimits.put(Factory.class, new Long(10));
		resourceLimits.put(Refinery.class, new Long(10));
		resourceLimits.put(DefenseTurret.class, new Long(10));
		resourceLimits.put(Engineer.class, new Long(10));
	}
	
	private void modifyResourceLimitForClass(Class c, Long l)
	{
		resourceLimits.remove(c);
		resourceLimits.put(c, l);
	}*/
	
	private void println(String s)
	{
		if (dEnabled)
			System.out.println("StalinArmy: "+s);
	}

	public void drawUI(GL gl) {}
	public Camera getCamera() { return null; }
	
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
			int ret;
			
			//This REALLY could be optimized
			if (uHash.get(u) == null)
			{
				uHash.put(u, new UnitContext(u));
				ResourceDeposit ird = getClosestResourceDepositAbs(u.getLocation(), w);
				rHash.get(ird).units.add(u);
				uHash.get(u).assocR = rHash.get(ird);
			}
			
			if (u.getCurrentAction() != null) continue;
			
			if (u instanceof Harvester)
			{
				ResourceDeposit rd = getClosestResourceDeposit(u.getLocation(), w);
				if (rd == null)
					rd = getClosestResourceDepositAbs(u.getLocation(), w);
				double[][] rect = getEnemyRect(w.getOwners()[0], w);
				if (getUnitCount(Harvester.class) > 4)
				{
					if (currSet != 5)
					{
						uHash.get(u).special = currSet++;
					}
				}
				switch (uHash.get(u).special)
				{
				case 1:
					moveUnit(u, rect[0][0], rect[0][1], w);
					break;
				case 2:
					moveUnit(u, rect[1][0], rect[0][1], w);
					break;
				case 3:
					moveUnit(u, rect[1][0], rect[1][1], w);
					break;
				case 4:
					moveUnit(u, rect[0][0], rect[1][1], w);
					break;
				}
				if (uHash.get(u).special != 0)
				{
					/* We make special units indestructable */
					u.setLife(1000000000);
					continue;
				}
				
				if (rd != null)
					gatherResources(u, rd, w);
			}
			if (u instanceof Leader || u instanceof Engineer)
			{
				int fc = 0;
				int rc;
				if (getUnitCount(Refinery.class) != 0)
					fc = maintainUnitCount(w, u, Factory.class, productionMax * factoryMultiplier, 50);
				
				if (getUnitCount(Refinery.class) != 0 && getUnitCount(Factory.class) != 0)
					maintainUnitCount(w, u, DefenseTurret.class, productionMax * turretsPerBase, 50);
				
				rc = maintainUnitCount(w, u, Refinery.class, productionMax, 50);
				
				ResourceDeposit rd = getClosestUnusedRD(u.getLocation(), w);
				if (rd != null && (fc == 0 || rc == 0))
				{
					moveUnitRandomlyAroundRD(u, w, rd);
				}
				
				if (fc == -1 && rc == -1 && rd != null)
				{
				    uHash.get(u).assocR.inhabited = true;
					productionMax++;

					println("Increasing factory/refinery quota: "+productionMax);
				}
			}
			if (u instanceof Tank)
			{
				moveUnitRandomlyAroundArea(u, w, u.getLocation(), 50);
			}
			if (u instanceof Factory)
			{	
				if (getUnitCount(Harvester.class) >= minHarvester)
					maintainUnitCount(w, u, Engineer.class, getResourceDeposits(w).size(), 0);
				maintainUnitCount(w, u, Harvester.class, minHarvester * productionMax, 0);
				int tc = maintainUnitCount(w, u, Tank.class, tanksPerBase * productionMax, 0);
				if (tc == -1)
					tanksPerBase++;
			}
		}
		
		ArrayList<UnitGroup> rmList = new ArrayList<UnitGroup>();
		for (UnitGroup ug : swarmGroups)
		{
			boolean groupDead = true;
			for (Unit ugu : ug.u)
			{
				if (ugu.getLife() > 0)
					groupDead = false;
			}
			if (groupDead)
			{
				for (Unit ugu : ug.u)
				{
					uHash.remove(ugu);
				}
				tanksPerBase++;
				minSwarmSize *= 1.25;
				rmList.add(ug);
				println("GROUP DEAD");
				println("Tanks per base: "+tanksPerBase);
				println("Min swarm size: "+minSwarmSize);
			}
		}
		/* Remove the dead groups */
		for (UnitGroup ug : rmList)
		{
			swarmGroups.remove(ug);
		}
		
		int usableTankCount = 0;
		
		ArrayList<ResourceDeposit> rds = getResourceDeposits(w);
		for (ResourceDeposit prd : rds)
		{
			rHash.get(prd).inhabited = false;
		}
		
		i = units.iterator();
		while (i.hasNext())
		{
			Unit un = i.next();
			if (un instanceof Tank && uHash.get(un).swarmGroup == null)
				usableTankCount++;
			if (un instanceof Factory)
				uHash.get(un).assocR.inhabited = true;
		}
		
		if (usableTankCount != lastTankCount)
		{
			println("Usable tank count: "+usableTankCount);
			//println("Minimum swarm count: "+minSwarmSize);
			lastTankCount = usableTankCount;
		}
		
		i = units.iterator();
		int targetCount = usableTankCount - minSwarmSize;
		
		if (targetCount > 0)
		{
			ArrayList<Unit> sw = new ArrayList<Unit>();
			UnitGroup swarm = new UnitGroup(sw);
			while (i.hasNext() && usableTankCount > targetCount)
			{
				Unit un = i.next();
				if (un instanceof Tank && uHash.get(un).swarmGroup == null)
				{
					double[][] rect = getEnemyRect(getEnemyOwners(w)[0], w);
					moveUnitRandomlyAroundRect(un, w, rect[0], rect[1][0], rect[1][1]);
					
					uHash.get(un).swarmGroup = swarm;
					swarm.u.add(un);
					usableTankCount--;
				}
			}
			swarmGroups.add(swarm);
			println("Swarming with "+swarm.u.size()+" units");
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
	
	@SuppressWarnings("unchecked")
	private int maintainUnitCount(World w, Unit u, Class c, int unitCount, int randMov)
	{
		boolean ret = true;
		int i = 0;
		
		if (getUnitCount(c) >= unitCount)
			return -1;
		
		/*if ((unitCount - getUnitCount(c)) * resPerUnit < (resourceLimits.get(c) / 100) * me.getResources())
		{
			println("Construction not allowed due to resource shortage");
			println("Units requested: "+(unitCount - getUnitCount(c)));
			println("Resource limit: "+(resourceLimits.get(c) / 100) * me.getResources());
			return -2;
		}*/
		
		for (int x = getUnitCount(c); x < unitCount && ret; x++)
		{
			if (randMov != 0)
				moveUnitRandomlyAroundArea(u, w, u.getLocation(), randMov);
			ret = buildUnit(c, u, w);
			if (ret)
				i++;
			//println("Building "+c+": " + (ret ? "Success" : "Failed"));
		}
		
		return i;
	}
	private Unit getFirstBase()
	{
		ArrayList<Unit> u = getUnits().get(Factory.class);
		if (u.isEmpty()) return null;
		return u.get(0);
	}
	private ResourceDeposit getClosestUnusedRD(double[] p, World w)
	{
		ArrayList<ResourceDeposit> deposits = getResourceDeposits(w);
		int closest = -1;
		double dist = Double.MAX_VALUE;
		for(int i = 0; i < deposits.size(); i++)
		{
			ResourceDeposit rd = deposits.get(i);
			if (rHash.get(rd) == null)
				rHash.put(rd, new ResourceContext(rd));
			boolean danger = false;
			for (int j = 0; j < w.getOwners().length; j++)
			{
				Owner o = w.getOwners()[j];
				if (o == me) continue;
				
				double[][] rect = getEnemyRect(o, w);
				double[] loc = rd.getLocation();
				if (loc[0] >= rect[0][0] && loc[0] <= rect[1][0] &&
					loc[1] >= rect[0][1] && loc[1] >= rect[1][1])
				{
					//println("Dangerous rd: "+loc[0]+", "+loc[1]);
					danger = true;
					break;
				}
			}
			double thisDist = MathUtil.distance(p[0], p[1], rd.getLocation()[0], rd.getLocation()[1]);
			if(thisDist < dist && !rHash.get(rd).inhabited && !danger)
			{
				closest = i;
				dist = thisDist;
			}
		}
		
		if (closest == -1)
			return null;
		
		//println("New base index: "+closest);
		
		return deposits.get(closest);
	}
	private ResourceDeposit getClosestResourceDepositAbs(double[] p, World w)
	{
		ArrayList<ResourceDeposit> deposits = getResourceDeposits(w);
		int closest = -1;
		double dist = Double.MAX_VALUE;
		for(int i = 0; i < deposits.size(); i++)
		{
			ResourceDeposit rd = deposits.get(i);
			if (rHash.get(rd) == null)
				rHash.put(rd, new ResourceContext(rd));
			double thisDist = MathUtil.distance(p[0], p[1], rd.getLocation()[0], rd.getLocation()[1]);
			if(thisDist < dist)
			{
				closest = i;
				dist = thisDist;
			}
		}
		
		return deposits.get(closest);
	}
	private ResourceDeposit getClosestResourceDeposit(double[] p, World w)
	{
		ArrayList<ResourceDeposit> deposits = getResourceDeposits(w);
		int closest = -1;
		double dist = Double.MAX_VALUE;
		for(int i = 0; i < deposits.size(); i++)
		{
			ResourceDeposit rd = deposits.get(i);
			if (rHash.get(rd) == null)
				rHash.put(rd, new ResourceContext(rd));
			boolean danger = false;
			for (int j = 0; j < w.getOwners().length; j++)
			{
				Owner o = w.getOwners()[j];
				if (o == me) continue;
				
				double[][] rect = getEnemyRect(o, w);
				double[] loc = rd.getLocation();
				if (loc[0] >= rect[0][0] && loc[0] <= rect[1][0] &&
					loc[1] >= rect[0][1] && loc[1] >= rect[1][1])
				{
					//println("Dangerous rd: "+loc[0]+", "+loc[1]);
					danger = true;
					break;
				}
			}
			double thisDist = MathUtil.distance(p[0], p[1], rd.getLocation()[0], rd.getLocation()[1]);
			if(thisDist < dist && rd.getTotalResources() > 0 && !danger)
			{
				closest = i;
				dist = thisDist;
			}
		}
		
		if (closest == -1)
			return null;
		
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
	private void moveUnitRandomlyAroundRD(Unit u, World w, ResourceDeposit rd)
	{
		double[] modloc = new double[] {rd.getLocation()[0], rd.getLocation()[1]};
		
		modloc[0] -= rd.getRadius();
		modloc[1] -= rd.getRadius();
		
		moveUnitRandomlyAroundArea(u, w, modloc, (int)(rd.getRadius() * 2));
	}
	public void moveUnitRandomly(Unit u, World w)
	{
		moveUnitSafely(u, rand.nextDouble()*w.getMapWidth(), rand.nextDouble()*w.getMapHeight(), w);
	}
	public void moveUnitRandomlyAroundArea(Unit u, World w, double[] p, int bounds)
	{
			moveUnitSafely(u, p[0]-bounds+rand.nextDouble()*bounds*2, p[1]-bounds+rand.nextDouble()*bounds*2, w);
	}
	public void moveUnitRandomlyAroundRect(Unit u, World w, double[] p, double xbound, double ybound)
	{
			moveUnitSafely(u, p[0]-xbound+rand.nextDouble()*xbound*2, p[1]-ybound+rand.nextDouble()*ybound*2, w);
	}
	private void moveUnitSafely(Unit u, double x, double y, World w)
	{
		for (Owner o : w.getOwners())
		{
			if (o == me) continue;
			double[][] rect = getEnemyRect(o, w);
			if (x >= rect[0][0] && x <= rect[1][0] &&
				y >= rect[0][1] && y <= rect[1][1])
			{
				if (u.getWeapon() != null)
				{
					//println("Armed intersection with hostile area allowed");
					moveUnit(u, x, y, w);
				}
				else
				{
					//println("Unarmed intersection with hostile area denied");
				}
				return;
			}
		}
		
		moveUnit(u, x, y, w);
	}
	private double[][] getEnemyFrontLine(Owner o, World w, double[] loc)
	{
		double[][] recta = getEnemyRect(o, w), line = new double[2][2], rectb = new double[4][2];
		double closesta = Double.MAX_VALUE, closestb = Double.MAX_VALUE;
		
		if (recta == null)
			return null;
		
		rectb[0] = recta[0];
		rectb[1][0] = recta[1][0];
		rectb[1][1] = recta[0][1];
		rectb[2] = recta[1];
		rectb[3][0] = recta[0][0];
		rectb[3][1] = recta[1][1];
		
		for (int i = 0; i < rectb.length; i++)
		{
			double dist = MathUtil.distance(loc[0], loc[1], rectb[i][0], rectb[i][1]);
			if (dist < closesta)
			{
				closesta = dist;
				line[0] = rectb[i];
			}
			else if (dist < closestb)
			{
				closestb = dist;
				line[1] = rectb[i];
			}
		}
		
		if (closesta == Double.MAX_VALUE || closestb == Double.MAX_VALUE)
		{
			println("Problem detected: Front line code");
			while (true);
		}
		
		return line;
	}
	//Returns 2 arrays of 2 points which represent the upper-left most point and the lower-right most point
	public  double[][] getEnemyRect(Owner o, World w)
	{
		double[][] ret = new double[2][2];
		
		ret[0][0] = Double.MAX_VALUE;
		ret[0][1] = Double.MAX_VALUE;
		ret[1][0] = 0;
		ret[1][1] = 0;
		
		LinkedList<Unit> units = getEnemyUnits(w).get(o);
		for (Unit u : units)
		{
			/* We don't care about it if it is unarmed */
			if (u.getWeapon() == null)
				continue;
			
			if (u.getLocation()[0] < ret[0][0])
				ret[0][0] = u.getLocation()[0];
			
			if (u.getLocation()[1] < ret[0][1])
				ret[0][1] = u.getLocation()[1];

			if (u.getLocation()[0] > ret[1][0])
				ret[1][0] = u.getLocation()[0];
			
			if (u.getLocation()[1] > ret[1][1])
				ret[1][1] = u.getLocation()[1];
		}
		
		ret[0][0] -= borderMod;
		if (ret[0][0] < 0)
			ret[0][0] = 0;
		
		ret[0][1] -= borderMod;
		if (ret[0][1] < 0)
			ret[0][1] = 0;
		
		ret[1][0] += borderMod;
		if (ret[1][0] > w.getMapWidth())
			ret[1][0] = w.getMapWidth();
		
		ret[1][1] += borderMod;
		if (ret[1][1] > w.getMapHeight())
			ret[1][1] = w.getMapHeight();
		
		//println("("+ret[0][0]+","+ret[0][1]+") ("+ret[1][0]+","+ret[1][1]+")");
		
		return ret;
	}
	@SuppressWarnings("unchecked")
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

class ResourceContext {
	public ArrayList<Unit> units;
	public ResourceDeposit r;
	public boolean inhabited;
	
	public ResourceContext(ResourceDeposit r)
	{
		this.r = r;
		units = new ArrayList<Unit>();
		inhabited = false;
	}
}

class UnitContext {
	public Unit u;
	public UnitGroup swarmGroup;
	public int nextAction;
	public ResourceContext assocR;
	public int special;
	
	public UnitContext(Unit u)
	{
		this.u = u;
		this.swarmGroup = null;
		this.nextAction = 0;
		this.assocR = null;
	}
}

class UnitGroup {
	public ArrayList<Unit> u;
	public long stTime;
	
	public UnitGroup(ArrayList<Unit> u)
	{
		this.u = u;
	}
}
