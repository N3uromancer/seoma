package gameEngine.world.unit;

import java.util.HashMap;

import ui.userIO.userInput.UserInput;
import utilities.SpatialPartition;

import ai.UserAI;

import gameEngine.RunStatistics;
import gameEngine.world.World;

public class UnitEngine
{
	/**
	 * stores units controlled by users
	 */
	HashMap<Byte, Unit> uUnits = new HashMap<Byte, Unit>();
	SpatialPartition u;
	
	public UnitEngine(World w)
	{
		u = new SpatialPartition(0, 0, w.getWidth(), w.getHeight(), 20, 75, 100);
	}
	public void updateUnitEngine(UserInput[] ui, double tdiff)
	{
		long start = System.currentTimeMillis();
		for(int i = 0; i < ui.length; i++)
		{
			UserAI uai = (UserAI)uUnits.get(ui[i].getOwner()).getAI();
			uai.interpretUserInput(ui[i]);
		}
		RunStatistics.putTime(getClass().getName(), "interpret user actions", System.currentTimeMillis()-start);
	}
}
