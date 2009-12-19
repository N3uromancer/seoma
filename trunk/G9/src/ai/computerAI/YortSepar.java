package ai.computerAI;

import gameEngine.world.World;

import gameEngine.world.animation.animations.Explosion;
import gameEngine.world.owner.Owner;
import gameEngine.world.resource.ResourceDeposit;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.units.*;
import gameEngine.world.unit.unitModifiers.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.media.opengl.GL;
import utilities.MathUtil;
import ai.AI;
import ui.userIO.userInput.UserInput;
import utilities.Camera;

public class YortSepar extends AI
{
	Random rand = new Random();
	public YortSepar(Owner o)
	{
		super(o);
	}
	public Camera getCamera()
	{
		return null;
	}
	private void commandLeader(Unit u, World w)
	{
		if(u.getCurrentAction() == null)
		{
			if(getUnits().get(Factory.class) == null || getUnits().get(Factory.class).size() == 0)
			{
				buildUnit(Factory.class, u, w);
				buildUnit(Refinery.class, u, w);
				buildUnit(Factory.class, u, w);
				buildUnit(Factory.class, u, w);
				buildUnit(Factory.class, u, w);
				buildUnit(Factory.class, u, w);
			}
		}
	}
	private void commandFactory(Unit u, World w)
	{
		if(u.getCurrentAction() == null)
		{
			if(getUnits().get(Harvester.class) == null || getUnits().get(Harvester.class).size() < 10)
			{
				buildUnit(Harvester.class, u, w);
			}
			else
			{
				buildUnit(Tank.class, u, w);
				buildUnit(Harvester.class, u, w);
			}
				
		}
	}
	public void performAIFunctions(World w, ArrayList<UserInput> ui)
	{
		LinkedList<Unit> units = getUnits(w);
		Iterator<Unit> i = units.iterator();
		while(i.hasNext())
		{
			Unit u = i.next();
			if(u.getCurrentAction() == null)
			{
				if(u instanceof Leader)
				{
					commandLeader(u, w);
				}
				if(u instanceof Factory)
				{
					commandFactory(u, w);
				}
				if(u instanceof Gatherer)
				{
					gatherResources(u, getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w), w);
				}
				if(u instanceof Tank)
				{
					if (getUnits().get(Tank.class).size() < 7)
						moveUnitRandomlyAroundArea(u, w, getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation(), 20);
					else
					{
						Unit target = null;
						Iterator<Owner> oi = getEnemyUnits(w).keySet().iterator(); //owner iterator
						while(oi.hasNext())
						{
							target = getEnemyUnits(w).get(oi.next()).getFirst();
						}
						if(target != null)
						{
							moveUnitRandomlyAroundArea(u, w, target.getLocation(), 100);
						}
					}
				}
			}
		}
	}
	public void moveUnitRandomly(Unit u, World w)
	{
		if(u.getCurrentAction() == null)
			moveUnit(u, rand.nextDouble()*w.getMapWidth(), rand.nextDouble()*w.getMapHeight(), w);
	}
	public void moveUnitRandomlyAroundArea(Unit u, World w, double[] p, int bounds)
	{
		if(u.getCurrentAction() == null)
			moveUnit(u, p[0]-bounds+rand.nextDouble()*bounds*2, p[1]-bounds+rand.nextDouble()*bounds*2, w);
	}
	public ResourceDeposit getClosestResourceDeposit(double x, double y, World w)
	{
		ArrayList<ResourceDeposit> deposits = getResourceDeposits(w);
		int closest = -1;
		double dist = 999999;
		for(int i = 0; i < deposits.size(); i++)
		{
			ResourceDeposit rd = deposits.get(i);
			double thisDist = MathUtil.distance(x, y, rd.getLocation()[0], rd.getLocation()[1]);
			if(thisDist < dist)
			{
				closest = i;
				dist = thisDist;
			}
		}
		return deposits.get(closest);
	}
	public void drawUI(GL gl){}
}