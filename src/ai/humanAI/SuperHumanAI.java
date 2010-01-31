package ai.humanAI;

import gameEngine.world.World;
import gameEngine.world.action.actions.Move;
import gameEngine.world.owner.Owner;
import gameEngine.world.resource.ResourceDeposit;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.units.DefenseTurret;
import gameEngine.world.unit.units.Engineer;
import gameEngine.world.unit.units.Factory;
import gameEngine.world.unit.units.Harvester;
import gameEngine.world.unit.units.Refinery;
import gameEngine.world.unit.units.Tank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;

import ui.userIO.userInput.*;

import utilities.Camera;
import utilities.MathUtil;
import ai.AI;
import ai.aiModule.CameraModule;

public class SuperHumanAI extends AI {

	/* Generic AI variables */
	CameraModule cm;
	Owner me;
	
	/* Mouse press location */
	int[] MPSt;
	
	/* Mouse release location */
	int[] MPEn;
	
	/* Debugging enabled/disabled */
	boolean debug = false;
	
	public SuperHumanAI(Owner o)
	{
		super(o);
		me = o;
		cm = new CameraModule('w', 'd', 's', 'a', 'r', 'f', 1000.0, 1.0);
		registerAIModule(cm);
	}
	
	private void DbgPrint(String s)
	{
		if (debug)
			System.out.println(s);
	}

	public void drawUI(GL gl) {}

	public Camera getCamera()
	{
		return cm.getCamera();
	}

	@SuppressWarnings("unchecked")
	protected void performAIFunctions(World w,
			HashMap<Class<? extends UserInput>, ArrayList<UserInput>> ui)
	{
		if (ui != null)
		{
			Object[] inputs = ui.values().toArray();
			for (int i = 0; i < inputs.length; i++)
			{
				processUserInput(w, (ArrayList<UserInput>)inputs[i]);
			}
		}

		autofunction(w);
	}
	
	/* Perform automatic functions for certain units */
	private void autofunction(World w)
	{
		ArrayList<Unit> deselUnits = getDeselectedUnits(w);
		
		for (Unit u : deselUnits)
		{
			if (u.getCurrentAction() != null)
				continue;
			
			/* If we are a harvester and we aren't currently selected
			 * or doing anything we will gather resources
			 */
			if (u instanceof Harvester)
			{
				ResourceDeposit rd = getClosestResourceDeposit(u.getLocation(), w);
				if (rd != null)
					gatherResources(u, rd, w);
			}
		}
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
			if(thisDist < dist && rd.getTotalResources() > 0)
			{
				closest = i;
				dist = thisDist;
			}
		}
		
		if (closest == -1)
			return null;
		
		return deposits.get(closest);
	}
	
	/* We clear actions before moving because it makes the game seem more
	 * responsive to the user
	 */
	private void moveUnitEx(Unit u, int[] loc, World w)
	{
		u.clearActions();
		moveUnit(u, loc[0], loc[1], w);
	}
	/* A simple method to convert an integer array into a string */
	private String loc(int[] loc)
	{
		if (loc != null)
			return "("+loc[0]+", "+loc[1]+")";
		else
			return "<NULL>";
	}
	/* The main method that does the bulk of our work by interpreting
	 * the user's actions
	 */
	private void processUserInput(World w, ArrayList<UserInput> i)
	{
		for (UserInput in : i)
		{
			int[] ml = null;
			
			/* We have to correct the mouse offset because it is relative to the camera */
			if (in instanceof MouseInput)
				ml = correctMouseOffset(w, ((MouseInput) in).getLocation());
			
			if (in instanceof MousePress)
			{
				MPSt = ml;
				DbgPrint("MP: "+loc(MPSt));
			}
			else if (in instanceof MouseRelease) {}
			else if (in instanceof MouseDrag)
			{
				DbgPrint("MD: "+loc(ml)+" "+loc(MPSt));
				if (MPSt != null)
					selectUnits(w, MPSt, ml);
			}
			else if (in instanceof MouseClick)
			{
				DbgPrint("MC: "+loc(ml));
				if (((MouseClick) in).isRightClick())
					deselectAll(w);
				else if (!selectUnitsByPoint(w, ml))
					moveSelectedUnits(w, ml);
			}
			else if (in instanceof KeyPress)
			{
				DbgPrint("KP: "+((KeyPress)in).getCharacter());
				switch (((KeyPress) in).getCharacter())
				{
				case 'f':
				case 'F':
					buildSelectedUnits(w, Factory.class);
					break;
					
				case 'r':
				case 'R':
					buildSelectedUnits(w, Refinery.class);
					break;
					
				case 'h':
				case 'H':
					buildSelectedUnits(w, Harvester.class);
					break;
					
				case 't':
				case 'T':
					buildSelectedUnits(w, Tank.class);
					break;
					
				case 'u':
				case 'U':
					buildSelectedUnits(w, DefenseTurret.class);
					break;
					
				case 'e':
				case 'E':
					buildSelectedUnits(w, Engineer.class);
					break;
				}
			}
			else if (in instanceof KeyRelease) {}
			else
			{
				DbgPrint("Unknown input!");
				while(true);
			}
		}
	}
	
	/* Correct the mouse offset because it is relative to the camera */
	private int[] correctMouseOffset(World w, int[] orig)
	{
		orig[0] += cm.getCamera().getLocation()[0];
		orig[1] += cm.getCamera().getLocation()[1];
		
		return orig;
	}
	
	/* Tries to build a specified unit type using all selected units */
	private void buildSelectedUnits(World w, Class<? extends Unit> c)
	{
		ArrayList<Unit> selUnits = getSelectedUnits(w);
		
		for (Unit u : selUnits)
		{
			buildUnit(c, u, w);
		}
	}
	
	/* Deselects all units */
	private void deselectAll(World w)
	{
		ArrayList<Unit> selUnits = getSelectedUnits(w);
		
		for (Unit u : selUnits)
		{
			u.setSelected(false);
		}
	}
	
	/* Moves all selected units to a location (See also MoveUnitEx) */
	private void moveSelectedUnits(World w, int[] loc)
	{
		ArrayList<Unit> selUnits = getSelectedUnits(w);
		
		for (Unit u : selUnits)
		{
			moveUnitEx(u, loc, w);
		}
	}
	
	/* Returns an ArrayList of all deselected units */
	private ArrayList<Unit> getDeselectedUnits(World w)
	{
		Iterator<Unit> ui = w.getUnitEngine().getUnitList(me).iterator();
		ArrayList<Unit> ret = new ArrayList<Unit>();
		while (ui.hasNext())
		{
			Unit u = ui.next();
			if (!u.isSelected())
				ret.add(u);
		}
		
		return ret;
	}
	
	/* Returns an ArrayList of all selected units */
	private ArrayList<Unit> getSelectedUnits(World w)
	{
		Iterator<Unit> ui = w.getUnitEngine().getUnitList(me).iterator();
		ArrayList<Unit> ret = new ArrayList<Unit>();
		while (ui.hasNext())
		{
			Unit u = ui.next();
			if (u.isSelected())
				ret.add(u);
		}
		
		return ret;
	}
	
	/* Returns a proper rect array based on 2 points */
	private int[][] getRectFromPts(int[] a, int[] b)
	{
		int[][] ret = new int[2][2];
		
		ret[0][0] = Math.min(a[0], b[0]);
		ret[1][0] = Math.max(a[0], b[0]);
		
		ret[0][1] = Math.min(a[1], b[1]);
		ret[1][1] = Math.max(a[1], b[1]);
		
		return ret;
	}
	
	/* Selects any units that we click on by testing to see if the click
	 * was within their bounds
	 */
	private boolean selectUnitsByPoint(World w, int[] a)
	{
		Iterator<Unit> ui = w.getUnitEngine().getUnitList(me).iterator();
		boolean ret = false;
		while (ui.hasNext())
		{
			Unit u = ui.next();
			double[] uLoc = u.getLocation();
			
			if (a[0] >= uLoc[0] && a[0] <= uLoc[0] + u.getWidth() &&
				a[1] >= uLoc[1] && a[1] <= uLoc[1] + u.getHeight())
			{
				u.setSelected(!u.isSelected());
				ret = true;
			}
		}
		
		return ret;
	}
	/* Selects units based on 2 points that form a selection rectangle */
	private void selectUnits(World w, int[] a, int[] b)
	{
		Iterator<Unit> ui = w.getUnitEngine().getUnitList(me).iterator();
		int[][] rect = getRectFromPts(a, b);
		while (ui.hasNext())
		{
			Unit u = ui.next();
			double[] uLoc = u.getLocation();
			
			if (uLoc[0] >= rect[0][0] && uLoc[0] <= rect[1][0] &&
				uLoc[1] >= rect[0][1] && uLoc[1] <= rect[1][1])
			{
				u.setSelected(true);
			}
			else
			{
				u.setSelected(false);
			}
		}
	}
}
