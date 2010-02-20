package ai.computerAI;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.media.opengl.GL;

import ui.userIO.userInput.UserInput;
import utilities.Camera;
import utilities.MathUtil;
import world.World;
import world.owner.Owner;
import world.resource.ResourceDeposit;
import world.unit.Unit;
import world.unit.unitModifiers.Gatherer;
import world.unit.units.DefenseTurret;
import world.unit.units.Engineer;
import world.unit.units.Factory;
import world.unit.units.Harvester;
import world.unit.units.Leader;
import world.unit.units.Refinery;
import world.unit.units.Tank;
import ai.AI;
import ai.aiModule.CameraModule;

public class YortSepar extends AI
{
	Random rand = new Random();
	CameraModule cm = new CameraModule('w', 'd', 's', 'a', 'r', 'f', 1000.0, 1.0);
	
	public YortSepar(Owner o)
	{
		super(o);
		registerAIModule(cm);
	}
	public Camera getCamera()
	{
		return cm.getCamera();
	}
	private void commandLeader(Unit u, World w)
	{
		if(u.getCurrentAction() == null)
		{
			if(getUnits().get(Factory.class) == null || getUnits().get(Factory.class).size() == 0)
			{
				buildUnit(Factory.class, u, w);
				buildUnit(Refinery.class, u, w);
			}
			else
			{
				buildUnit(Refinery.class, u, w);
				buildUnit(Factory.class, u, w);
				buildUnit(Factory.class, u, w);
				buildUnit(Factory.class, u, w);
				//buildUnit(DefenseTurret.class, u, w);
				buildUnit(Factory.class, u, w);
				buildUnit(Refinery.class, u, w);
				buildUnit(Refinery.class, u, w);
				buildUnit(DefenseTurret.class, u, w);
				buildUnit(DefenseTurret.class, u, w);
				buildUnit(DefenseTurret.class, u, w);
				buildUnit(Refinery.class, u, w);
				buildUnit(Refinery.class, u, w);
				buildUnit(DefenseTurret.class, u, w);
				buildUnit(DefenseTurret.class, u, w);
				buildUnit(DefenseTurret.class, u, w);
				buildUnit(DefenseTurret.class, u, w);
				buildUnit(DefenseTurret.class, u, w);
			}
		}
	}
	private void commandEngineer(Unit u, World w)
	{
		if(u.getCurrentAction() == null)
		{
			if(getUnits().get(Refinery.class).size() == 0)
			{
				ResourceDeposit rd = getClosestResourceDeposit(u.getLocation(), w);
				double[] s = rd.getLocation();
				moveUnitRandomlyAroundArea(u, w, new double[]{s[0]-rd.getRadius()*2, s[1]-rd.getRadius()*2}, (int)(rd.getRadius()*4));
				buildUnit(Refinery.class, u, w);
			}
			moveUnitRandomlyAroundArea(u, w, getClosestResourceDeposit(u.getLocation(), w).getLocation(), 100);
			buildUnit(DefenseTurret.class, u, w);
			moveUnitRandomlyAroundArea(u, w, getClosestResourceDeposit(u.getLocation(), w).getLocation(), 100);
			buildUnit(DefenseTurret.class, u, w);
			moveUnitRandomlyAroundArea(u, w, getClosestResourceDeposit(u.getLocation(), w).getLocation(), 100);
			buildUnit(Factory.class, u, w);
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
			else if(getUnits().get(Engineer.class) == null || getUnits().get(Engineer.class).size() == 0)
			{
				buildUnit(Engineer.class, u, w);
			}
			else
			{
				buildUnit(Tank.class, u, w);
				buildUnit(Tank.class, u, w);
				if(getUnits().get(Harvester.class).size() < 50)
				{
					buildUnit(Harvester.class, u, w);
				}
			}
				
		}
	}
	protected void performAIFunctions(World w, HashMap<Class<? extends UserInput>, ArrayList<UserInput>> ui)
	{
		LinkedList<Unit> units = getUnits(w);
		Iterator<Unit> i = units.iterator();
		while(i.hasNext())
		{
			Unit u = i.next();
			if(u.getCurrentAction() == null)
			{
				if(u instanceof Factory)
				{
					commandFactory(u, w);
				}
				else if(u instanceof Leader)
				{
					commandLeader(u, w);
				}
				else if(u instanceof Gatherer)
				{
					gatherResources(u, getClosestResourceDeposit(u.getLocation(), w), w);
				}
				else if(u instanceof Tank)
				{
					if (getUnits().get(Tank.class).size() < 200)
						moveUnitRandomlyAroundArea(u, w, getClosestResourceDeposit(u.getLocation(), w).getLocation(), 70);
					else
					{
						Unit target = null;
						Iterator<Owner> oi = getEnemyUnits(w).keySet().iterator(); //owner iterator
						while(oi.hasNext() && target == null)
						{
							try
							{
								target = getEnemyUnits(w).get(oi.next()).getFirst();
							}
							catch(NoSuchElementException e){}
						}
						if(target != null)
						{
							moveUnitRandomlyAroundArea(u, w, target.getLocation(), 100);
						}
					}
				}
				else if(u instanceof Engineer)
				{
					commandEngineer(u, w);
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
	/**
	 * gets the closest resource deposit
	 * @param p the point that resource deposits are being tested for distance to
	 * @param w
	 * @return returns the resource deposit closest to the passed point
	 */
	public ResourceDeposit getClosestResourceDeposit(double[] p, World w)
	{
		ArrayList<ResourceDeposit> deposits = getResourceDeposits(w);
		int closest = -1;
		double dist = 999999;
		for(int i = 0; i < deposits.size(); i++)
		{
			ResourceDeposit rd = deposits.get(i);
			double thisDist = MathUtil.distance(p[0], p[1], rd.getLocation()[0], rd.getLocation()[1]);
			if(thisDist < dist)
			{
				closest = i;
				dist = thisDist;
			}
		}
		return deposits.get(closest);
	}
	public void drawUI(GL gl){}
	@Override
	public void initialize(World w) {
		// TODO Auto-generated method stub
		
	}
}