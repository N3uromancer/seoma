package ai;

import java.util.ArrayList;
import java.util.LinkedList;

import gameEngine.world.World;
import gameEngine.world.action.actions.*;
import gameEngine.world.owner.Owner;
import gameEngine.world.unit.BuildOrder;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.unitModifiers.Builder;

import javax.media.opengl.GL;

import ui.userIO.userInput.UserInput;
import utilities.Camera;
import utilities.Location;

/**
 * defines the base methods for an ai, each ai also handles the user actions
 * of its owner (computer ais dont need to use these methods) including
 * mouse clicks, drags, etc
 * @author Jack
 *
 */
public abstract class AI
{
	protected Owner o;
	
	public AI(Owner o)
	{
		this.o = o;
	}
	public void buildUnit(Class<? extends Unit> u, Unit builder, World w)
	{
		if(builder instanceof Builder)
		{
			Builder b = (Builder)builder;
			if(b.canBuild(u))
			{
				BuildOrder bo = new BuildOrder(3, u, builder);
				w.getUnitEngine().registerBuildOrder(bo);
			}
		}
	}
	/**
	 * gets the units controlled by this ai
	 * @return returns a list of all controlled units
	 */
	public LinkedList<Unit> getUnits(World w)
	{
		return w.getUnitEngine().getUnitList(o);
	}
	/**
	 * returns the camera specifying the view of this ai for
	 * drawing purposes
	 * @return
	 */
	public abstract Camera getCamera();
	/**
	 * orders the passed unit to move to the passed game location,
	 * pushes a move order to the unit action stack
	 * @param u the unit being moved, its movement must be greater than zero
	 * @param x the x position the unit is being told to move to, must be in the map
	 * @param y the y position the unit is being told to move to, must be in the map
	 */
	public void moveUnit(Unit u, double x, double y, World w)
	{
		if(w.inWorld(x, y) && u.getMovement() > 0)
		{
			//System.out.println("unit ordered to move to ("+x+", "+y+")");
			double[] s = u.getLocation();
			Location[] l = w.getPathFinder().determinePath(s[0], s[1], x, y);
			//u.addAction(new Move(u, x, y));
			/*for(int i = 0; i < l.length; i++)
			{
				u.addAction(new Move(u, l[i].x, l[i].y));
			}*/
			u.addAction(new MoveList(u, l));
		}
	}
	/**
	 * the main AI method, called once by the SGEngine every iteration
	 * of the main thread
	 * @param w
	 * @param ui user input performed by this ai's owner
	 */
	public abstract void performAIFunctions(World w, ArrayList<UserInput> ui);
	/**
	 * draws a UI for this ai
	 * @param gl
	 */
	public abstract void drawUI(GL gl);
}
