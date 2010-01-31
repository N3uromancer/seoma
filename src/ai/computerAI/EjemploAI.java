package ai.computerAI;

/*
 * Example AI by Kyle
 * covers:
 * moving units
 * building units
 * gathering resources
 * attacking
 */

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

public class EjemploAI extends AI
{
	//Creates the generator for random numbers
	Random rand;
	//These are the camera settings, make sure to set them for your AI or it won't be drawn
	CameraModule cm = new CameraModule('w', 'd', 's', 'a', 'r', 'f', 1000.0, 1.0);
	//amount of tanks to get before attacking
	int attackSize = 10;
	
	public EjemploAI(Owner o)
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
		//global randomness seed, used for networking
		if(rand == null)
		{
			rand = new Random(w.getSeed());
		}
		
		LinkedList<Unit> units = getUnits(w);
		Iterator<Unit> i = units.iterator();
		//This cycles through every unit and, if it is inactive, gives it a new task
		while(i.hasNext())
		{
			Unit u = i.next();
			if(u.getCurrentAction() == null) //makes sure we only give new tasks to inactive units
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
	
	/*
	 * Here's where the actions of each individual unit are controlled
	 */
	public void manageLeader(Unit u, World w)
	{
		//if this is the beginning of the game, queue up some commands
		if(getUnits().get(Factory.class) == null || getUnits().get(Factory.class).size() == 0)
		{
			/* 
			 * Build a factory(to make units), a refinery to process resources, and a turret 
			 * for defense at the current location, as well as a second factory
			 * 
			 * Because there is no moveUnit() command anywhere in here, it will just build
			 * these buildings where it starts
			 */
			buildUnit(Factory.class, u, w);
			buildUnit(Refinery.class, u, w);
			buildUnit(Factory.class, u, w);
			buildUnit(DefenseTurret.class, u, w);
		}
	}
	public void manageFactory(Unit u, World w)
	{
		//If we have less than 10 harvesters, make more
		if(getUnits().get(Harvester.class) == null || getUnits().get(Harvester.class).size() < 10)
		{
			buildUnit(Harvester.class, u, w);
		}
		//afterwards, pure tanks
		else
		{
			buildUnit(Tank.class, u, w);
		}	
	}
	public void manageGatherer(Unit u, World w)
	{
		/*	This tells any Gatherer type unit (any unit that harvests resources)
		 * to just constantly gather resources from the resource deposit chosen
		 * by the getClosestResourceDeposit() method. That method can be tampered with
		 * to divide gatherers between deposits, or to abandon deposits with 
		 * nearby enemies or low resource counts
		 */
		gatherResources(u, getClosestResourceDeposit(u.getLocation(), w), w);
	}
	public void manageTank(Unit u, World w)
	{
		/* This is the method controlling the Tank units.
		 * it waits until it has more than attackSize Tanks, then
		 * moves them to the first enemy unit in the list
		 */

		if(getUnits().get(Tank.class).size() < attackSize)
		{
			moveUnitRandomlyAroundRect(u, w, getUnits().get(Factory.class).get(0).getLocation(), 100, 100);					
		}
		else
		{
			//this simply picks the first enemy on the list and moves the tanks there
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
	
	//This moves a unit somewhere random on the map, useful for spreading units across the map
	public void moveUnitRandomly(Unit u, World w)
	{
			moveUnit(u, rand.nextDouble()*w.getMapWidth(), rand.nextDouble()*w.getMapHeight(), w);
	}
	//this moves a unit randomly within a specified area, useful for covering a specific place
	public void moveUnitRandomlyAroundRect(Unit u, World w, double[] p, double xbound, double ybound)
	{
			moveUnit(u, p[0]-xbound+rand.nextDouble()*xbound*2, p[1]-ybound+rand.nextDouble()*ybound*2, w);
	}
	//This method finds the closest resource deposit based on the given location
	public ResourceDeposit getClosestResourceDeposit(double[] p, World w)
	{
		ArrayList<ResourceDeposit> deposits = getResourceDeposits(w);
		int closest = -1;
		double dist = Integer.MAX_VALUE;
		for(int i = 0; i < deposits.size(); i++)
		{
			ResourceDeposit rd = deposits.get(i);
			double thisDist = MathUtil.distance(p[0], p[1], rd.getLocation()[0], rd.getLocation()[1]);
			//this also checks to see if the deposit has enough resources
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