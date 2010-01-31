package ai.computerAI.computerEasy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import javax.media.opengl.GL;
import ui.userIO.userInput.UserInput;
import utilities.Camera;
import utilities.MathUtil;
import world.World;
import world.owner.Owner;
import world.resource.ResourceDeposit;
import world.unit.Unit;
import world.unit.unitModifiers.Gatherer;
import world.unit.units.*;
import ai.AI;
import ai.aiModule.CameraModule;

/**
 * a simple compy
 * 
 * abstracts the actual management of the units to simply coding
 * 
 * @author Jack
 *
 */
public class ComputerEasy extends AI
{
	CameraModule cm = new CameraModule('w', 'd', 's', 'a', 'r', 'f', 500.0, 1.0);
	
	ExpansionSiteManager esm = new ExpansionSiteManager();
	
	public ComputerEasy(Owner o)
	{
		super(o);
		registerAIModule(cm);
	}
	public void unitDestroyed(Unit u)
	{
		super.unitDestroyed(u);
		esm.logUnitDestroyed(u);
	}
	public void unitConstructed(Unit u)
	{
		super.unitConstructed(u);
		esm.associateUnit(u);
		//System.out.println(u+" constructed");
	}
	public Camera getCamera()
	{
		return cm.getCamera();
	}
	public void initialize(World w)
	{
		LinkedList<Unit> units = getUnits(w);
		HashSet<ResourceDeposit> rd = new HashSet<ResourceDeposit>();
		ExpansionSite expo = new ExpansionSite(getClosestResourceDeposit(units.getFirst().getLocation(), 0, rd, w), this, w);
		esm.addExpansionSite(expo);
		esm.associateUnit(units.getFirst(), expo);
		expo.buildUnit(Factory.class, true);
		expo.buildUnit(Refinery.class, true);
	}
	protected void performAIFunctions(World w, HashMap<Class<? extends UserInput>, ArrayList<UserInput>> ui)
	{
		LinkedList<Unit> units = getUnits(w);
		for(Unit u: units)
		{
			esm.manageUnit(u);
		}
		esm.manageSites();
	}
	public void moveUnitRandomly(Unit u, World w)
	{
			moveUnit(u, Math.random()*w.getMapWidth(), Math.random()*w.getMapHeight(), w);
	}
	/**
	 * moves a unit randomly in a rectangular region centered at the passed point
	 * @param u
	 * @param w
	 * @param p
	 * @param xbound the x "radius" of the rectangle, half the width
	 * @param ybound the y "radius" of the rectangle, half the height
	 */
	public void moveUnitInRect(Unit u, World w, double[] p, double xbound, double ybound)
	{
			moveUnit(u, p[0]-xbound+Math.random()*xbound*2, p[1]-ybound+Math.random()*ybound*2, w);
	}
	/**
	 * gets the closest resource deposit to the passed point that has above the passed minimum number of resources
	 * @param p
	 * @param minResources the minimum number of resources the deposit must have to be considered
	 * @param not the reousrce deposit cannot be contained in this set if it is to be returned
	 * @param w
	 * @return
	 */
	public ResourceDeposit getClosestResourceDeposit(double[] p, double minResources, HashSet<ResourceDeposit> not, World w)
	{
		ArrayList<ResourceDeposit> deposits = getResourceDeposits(w);
		int closest = -1;
		double dist = Integer.MAX_VALUE;
		for(int i = 0; i < deposits.size(); i++)
		{
			ResourceDeposit rd = deposits.get(i);
			double thisDist = MathUtil.distance(p[0], p[1], rd.getLocation()[0], rd.getLocation()[1]);
			//this also checks to see if the deposit has enough resources
			if(thisDist < dist && rd.getTotalResources() > minResources && !not.contains(rd))
			{
				closest = i;
				dist = thisDist;
			}
		}
		if(closest != -1)
		{
			return deposits.get(closest);
		}
		return null;
	}
	public void drawUI(GL gl){}
}