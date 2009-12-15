package ai.computerAI;

import gameEngine.world.World;

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
import utilities.Location;

public class RapestroyAI extends AI
{
	Random rand;
	
	public RapestroyAI(Owner o, Long seed)
	{
		super(o, seed);
		rand = new Random(seed);
	}
	public Camera getCamera()
	{
		return null;
	}
	public void performAIFunctions(World w, ArrayList<UserInput> ui)
	{
		System.out.println("resources: " + o.getResources());
		LinkedList<Unit> units = getUnits(w);
		Iterator<Unit> i = units.iterator();
		while(i.hasNext())
		{
			Unit u = i.next();
			if(u.getCurrentAction() == null)
			{
				if(u instanceof Leader)
				{
					//if(u.getLocation() != getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation())
						//moveUnit(u, getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation()[0], getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation()[1], w);
					if(getUnits().get(Refinery.class) == null)
						buildUnit(Refinery.class, u, w);
					else if(getUnits().get(Factory.class) == null)
						buildUnit(Factory.class, u, w);
					else if (getUnits().get(Factory.class).size() < 10 && getUnits().get(Harvester.class) != null && getUnits().get(Factory.class).size()+1 < getUnits().get(Harvester.class).size()*3)
						buildUnit(Factory.class, u, w);
					//else moveUnitRandomlyAroundArea(u, w, getUnits().get(Factory.class).get(0).getLocation()[0], getUnits().get(Factory.class).get(0).getLocation()[1], 20);
				}
				if(u instanceof Factory)
				{
					if(getUnits().get(Harvester.class) == null)
						buildUnit(Harvester.class, u, w);
					else if (getUnits().get(Harvester.class).size() < 30)
						buildUnit(Harvester.class, u, w);
					else if(getUnits().get(Tank.class) == null)
						buildUnit(Tank.class, u, w);
					else if (getUnits().get(Tank.class).size() < 60)
						buildUnit(Tank.class, u, w);
				}
				if(u instanceof Gatherer)
				{
					gatherResources(u, getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w), w);
				}
				if(u instanceof Tank)
				{
					if (getUnits().get(Tank.class).size() < 25)
						moveUnitRandomlyAroundArea(u, w, getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation()[0], getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation()[1], 20);
					else
						moveUnitRandomly(u, w);
				}
			}
		}
	}
	public void moveUnitRandomly(Unit u, World w)
	{
		if(u.getCurrentAction() == null)
			moveUnit(u, rand.nextDouble()*w.getMapWidth(), rand.nextDouble()*w.getMapHeight(), w);
	}
	public void moveUnitRandomlyAroundArea(Unit u, World w, double x, double y, int bounds)
	{
		if(u.getCurrentAction() == null)
			moveUnit(u, x-bounds+rand.nextDouble()*bounds*2, y-bounds+rand.nextDouble()*bounds*2, w);
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