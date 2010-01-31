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

/* !---------------------- THIS AI IS HENCEFORTH-ETH DISCONTINUED. ------------------------!
 * !--------------------- IT IS STILL HERE AS SOMEWHAT OF AN EXAMPLE ----------------------!
 * !--------------------- I GUESS IT JUST WASN'T RAPESTROYING ENOUGH ----------------------!
 * eventually this will be so out of date that we just remove it though.
 * but its spirit will live on in whatever I decide to name my new AI
 */

public class RapestroyAI extends AI
{
	Random rand;
	CameraModule cm = new CameraModule('w', 'd', 's', 'a', 'r', 'f', 1500.0, 1.0);
	
	public RapestroyAI(Owner o)
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
		
		//System.out.println("resources: " + o.getResources());
		LinkedList<Unit> units = getUnits(w);
		Iterator<Unit> i = units.iterator();
		while(i.hasNext())
		{
			Unit u = i.next();
			//w.getAnimationEngine().registerAnimation(new Explosion(u));
			if(u.getCurrentAction() == null)
			{
				if(u instanceof Leader)
				{
					//if(u.getLocation() != getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation())
						//moveUnit(u, getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation()[0], getClosestResourceDeposit(u.getLocation()[0], u.getLocation()[1], w).getLocation()[1], w);
					if(getUnits().get(Factory.class) == null || getUnits().get(Factory.class).size() == 0)
					{
						buildUnit(Factory.class, u, w);
						buildUnit(Refinery.class, u, w);
						buildUnit(Factory.class, u, w);
						buildUnit(Factory.class, u, w);
						buildUnit(Factory.class, u, w);
						moveUnitRandomlyAroundArea(u, w, u.getLocation(), 100);
						buildUnit(DefenseTurret.class, u, w);
						moveUnitRandomlyAroundArea(u, w, u.getLocation(), 100);
						buildUnit(DefenseTurret.class, u, w);
						moveUnitRandomlyAroundArea(u, w, u.getLocation(), 100);
						buildUnit(DefenseTurret.class, u, w);
						moveUnitRandomlyAroundArea(u, w, u.getLocation(), 100);
						buildUnit(DefenseTurret.class, u, w);
					}
					else
					{
						try
						{
							moveUnitRandomlyAroundArea(u, w, getUnits().get(Refinery.class).get(0).getLocation(), 100);
							buildUnit(Factory.class, u, w);
						}
						catch(IndexOutOfBoundsException e){}
					}
					//else moveUnitRandomlyAroundArea(u, w, getUnits().get(Factory.class).get(0).getLocation()[0], getUnits().get(Factory.class).get(0).getLocation()[1], 20);
				}
				if(u instanceof Factory)
				{
					if(getUnits().get(Harvester.class) == null || getUnits().get(Harvester.class).size() < 15)
					{
						buildUnit(Harvester.class, u, w);
					}
					else if(getUnits().get(Engineer.class) == null)
					{
						buildUnit(Engineer.class, u, w);
					}
					else
					{
						buildUnit(Tank.class, u, w);
						buildUnit(Harvester.class, u, w);
					}	
				}
				if(u instanceof Gatherer)
				{
					gatherResources(u, getClosestResourceDeposit(u.getLocation(), w), w);
				}
				if(u instanceof Engineer)
				{
					if(getUnits().get(DefenseTurret.class) == null || getUnits().get(DefenseTurret.class).size() < 5)
						moveUnitRandomlyAroundArea(u, w, getUnits().get(Factory.class).get(0).getLocation(), 100);
					else
						moveEngineer(u, w);						
					buildUnit(DefenseTurret.class, u, w);
				}
				if(u instanceof Tank)
				{
					if(getUnits().get(Tank.class).size() < 100)
					{
						moveUnitRandomlyAroundArea(u, w, getUnits().get(Factory.class).get(0).getLocation(), 100);					
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
							moveUnitRandomlyAroundArea(u, w, tu.getLocation(), 100);
						}
					}
				}
			}
		}
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
	public double[] getMidpoint(double[] a, double[] b)
	{
		double[] c = new double[2];
		c[0] = Math.abs(a[0] - b[0]);
		c[1] = Math.abs(a[1] - b[1]);
		return c;
	}
	//move unit within a space encompassing the base and resource deposit
	public void moveEngineer(Unit u, World w)
	{
		double[] center = getMidpoint(getUnits().get(Factory.class).get(0).getLocation(), getClosestResourceDeposit(getUnits().get(Factory.class).get(0).getLocation(), w).getLocation());
		double[] r = getClosestResourceDeposit(getUnits().get(Factory.class).get(0).getLocation(), w).getLocation();
		double[] f = getUnits().get(Factory.class).get(0).getLocation();
		moveUnitRandomlyAroundRect(u, w, center, Math.abs(r[0]-f[0])+50, Math.abs(r[1]-f[1])+50);
	}
	public ResourceDeposit getClosestResourceDeposit(double[] p, World w)
	{
		ArrayList<ResourceDeposit> deposits = getResourceDeposits(w);
		int closest = -1;
		double dist = 999999;
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
	public void drawUI(GL gl){}
}