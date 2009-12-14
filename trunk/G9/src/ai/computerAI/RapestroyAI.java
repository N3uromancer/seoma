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
import javax.media.opengl.GL;
import utilities.MathUtil;
import ai.AI;
import ui.userIO.userInput.UserInput;
import utilities.Camera;
import utilities.Location;

public class RapestroyAI extends AI
{
	int factories = 0;
	int harvesters = 0;
	
	public RapestroyAI(Owner o)
	{
		super(o);
	}
	public Camera getCamera()
	{
		return null;
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
					if(u.getLocation() != getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation())
						moveUnit(u, getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation()[0], getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation()[1], w);
					if(getUnits().get(Refinery.class) == null)
						buildUnit(Refinery.class, u, w);
					else if(getUnits().get(Factory.class) == null)
						buildUnit(Factory.class, u, w);
					//else moveUnitRandomlyAroundArea(u, w, getUnits().get(Factory.class).get(0).getLocation()[0], getUnits().get(Factory.class).get(0).getLocation()[1], 20);
				}
				if(u instanceof Factory)
				{
					if(getUnits().get(Harvester.class) == null)
						buildUnit(Harvester.class, u, w);
					else if (getUnits().get(Harvester.class).size() < 2)
						buildUnit(Harvester.class, u, w);
					if(getUnits().get(Tank.class) == null)
						buildUnit(Tank.class, u, w);
					else //if (getUnits().get(Tank.class).size() < 40)
						buildUnit(Tank.class, u, w);
				}
				if(u instanceof Gatherer)
				{
					//gatherResources(u, getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w), w);
				}
				if(u instanceof Tank)
				{
					if (getUnits().get(Tank.class).size() < 20)
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
			moveUnit(u, Math.random()*w.getMapWidth(), Math.random()*w.getMapHeight(), w);
	}
	public void moveUnitRandomlyAroundArea(Unit u, World w, double x, double y, int bounds)
	{
		if(u.getCurrentAction() == null)
			moveUnit(u, x-bounds+Math.random()*bounds*2, y-bounds+Math.random()*bounds*2, w);
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