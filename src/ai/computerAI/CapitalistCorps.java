package ai.computerAI;


import gameEngine.world.World;
import gameEngine.world.owner.Owner;
import gameEngine.world.resource.ResourceDeposit;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.units.*;
import gameEngine.world.unit.unitModifiers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.media.opengl.GL;
import utilities.MathUtil;
import ai.AI;
import ai.aiModule.CameraModule;
import ui.userIO.userInput.UserInput;
import utilities.Camera;

public class CapitalistCorps extends AI
{
	Random rand;
	CameraModule cm = new CameraModule('w', 'd', 's', 'a', 'r', 'f', 1000.0, 1.0);
	int attackSize = 10;
	
	public CapitalistCorps(Owner o)
	{
		super(o);
		registerAIModule(cm);
	}
	public Camera getCamera()
	{
		return cm.getCamera();
	}
	protected void performAIFunctions(World w, HashMap<Class<? extends UserInput>, ArrayList<UserInput>> ui)
	{
		if(rand == null)
		{
			rand = new Random(w.getSeed());
		}
		
		LinkedList<Unit> units = getUnits(w);
		Iterator<Unit> i = units.iterator();
		while(i.hasNext())
		{
			Unit u = i.next();
			if(u.getCurrentAction() == null) 
			{
				if(u instanceof Leader)
				{
					manageLeader(u, w);
				}
				else if(u instanceof Factory)
				{
					manageFactory(u, w);
				}
				else if(u instanceof Gatherer)
				{
					manageGatherer(u, w);
				}
				else if(u instanceof Tank)
				{
					manageTank(u, w);
				}
			}
		}
	}
	

	public void manageLeader(Unit u, World w)
	{
		if(getUnits().get(Factory.class) == null || getUnits().get(Factory.class).size() == 0)
		{
			buildUnit(Factory.class, u, w);
			buildUnit(Refinery.class, u, w);
			buildUnit(DefenseTurret.class, u, w);
		}
	}
	public void manageFactory(Unit u, World w)
	{
		if(getUnits().get(Harvester.class) == null || getUnits().get(Harvester.class).size() < 5)
		{
			buildUnit(Harvester.class, u, w);
		}
		else
		{
			buildUnit(Tank.class, u, w);
			buildUnit(Harvester.class, u, w);
		}	
	}
	public void manageGatherer(Unit u, World w)
	{
		gatherResources(u, getClosestResourceDeposit(u.getLocation(), w), w);
	}
	public void manageTank(Unit u, World w)
	{
		if(getUnits().get(Tank.class).size() < attackSize)
		{
			moveUnitRandomlyAroundRect(u, w, getUnits().get(Factory.class).get(0).getLocation(), 100, 100);					
		}
		else
		{
			Unit tu = null;
			Iterator<Owner> oi = getEnemyUnits(w).keySet().iterator();
			while(oi.hasNext() && tu == null)
			{
				try
				{
					tu = getEnemyUnits(w).get(oi.next()).getFirst();
				}
				catch(Exception e){}
			}
			if(tu != null)
			{
				moveUnitRandomlyAroundRect(u, w, tu.getLocation(), 50, 50);
			}
		}
	}
	
	public void moveUnitRandomly(Unit u, World w)
	{
			moveUnit(u, rand.nextDouble()*w.getMapWidth(), rand.nextDouble()*w.getMapHeight(), w);
	}
	public void moveUnitRandomlyAroundRect(Unit u, World w, double[] p, double xbound, double ybound)
	{
			moveUnit(u, p[0]-xbound+rand.nextDouble()*xbound*2, p[1]-ybound+rand.nextDouble()*ybound*2, w);
	}
	public ResourceDeposit getClosestResourceDeposit(double[] p, World w)
	{
		ArrayList<ResourceDeposit> deposits = getResourceDeposits(w);
		int closest = -1;
		double dist = Integer.MAX_VALUE;
		for(int i = 0; i < deposits.size(); i++)
		{
			ResourceDeposit rd = deposits.get(i);
			double thisDist = MathUtil.distance(p[0], p[1], rd.getLocation()[0], rd.getLocation()[1]);
			if(thisDist < dist && rd.getTotalResources() > 200)
			{
				closest = i;
				dist = thisDist;
			}
		}
		return deposits.get(closest);
	}
	public void drawUI(GL gl){}
}