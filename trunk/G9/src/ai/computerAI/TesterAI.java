package ai.computerAI;

import gameEngine.world.World;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.units.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.media.opengl.GL;
import ai.AI;
import ui.userIO.userInput.UserInput;
import utilities.Camera;
import utilities.Location;

public class TesterAI extends AI
{
	int factories = 0;
	int harvesters = 0;
	
	Random rand;
	
	public TesterAI(Owner o, Long seed)
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
		LinkedList<Unit> units = getUnits(w);
		Iterator<Unit> i = units.iterator();
		while(i.hasNext())
		{
			Unit u = i.next();
			if(u.getCurrentAction() == null && getUnits().get(Refinery.class) == null)
			{
				buildUnit(Refinery.class, u, w);
				randomlyMoveUnit(u, w);
				buildUnit(Factory.class, u, w);
			}
			/*if(factories == 0 && buildUnit(Factory.class, u, w))
			{
				factories++;
			}*/
			if(harvesters < 1 && buildUnit(Harvester.class, u, w))
			{
				harvesters++;
			}
			gatherResources(u, getResourceDeposits(w).get(1), w);
			if(u instanceof Harvester)
			{
				//System.out.println(u.getCurrentAction());
			}
			if(u.getCurrentAction() == null)
			{
				randomlyMoveUnit(u, w);
			}
		}
	}
	/**
	 * gets a random location within the map bounds
	 * @return returns a random location inside the game world
	 */
	private Location getRandomMapLocation(World w)
	{
		return new Location(rand.nextDouble()*w.getMapWidth(), rand.nextDouble()*w.getMapHeight());
	}
	private void randomlyMoveUnit(Unit u, World w)
	{
		Location l = getRandomMapLocation(w);
		//System.out.println(l);
		moveUnit(u, l.x, l.y, w);
	}
	public void drawUI(GL gl){}
}
