package ai;

import java.util.ArrayList;
import java.util.Iterator;

import ui.userIO.userInput.*;
import unit.Unit;

/**
 * accepts and interprets user input
 * @author Jack
 *
 */
public class UserAI extends AI
{
	double[] targetLocation; //where the user has clicked to move the unit
	
	public UserAI(Unit u)
	{
		super(u);
	}
	/**
	 * interprets user input that is directed to the unit
	 * under the management of this ai
	 * @param ui the list of user input that corresponds to this units owner
	 */
	public void interpretUserInput(ArrayList<UserInput> userInput)
	{
		Iterator<UserInput> i = userInput.iterator();
		while(i.hasNext())
		{
			UserInput ui = i.next();
			if(ui instanceof MouseClick)
			{
				MouseClick mc = (MouseClick)ui;
				targetLocation = new double[]{mc.getLocation()[0], mc.getLocation()[1]};
			}
		}
	}
	public void performCommands(double tdiff)
	{
		if(targetLocation != null)
		{
			move(tdiff, targetLocation);
			if(targetLocation[0] == u.getLocation()[0] && targetLocation[1] == u.getLocation()[1])
			{
				targetLocation = null;
			}
		}
	}
}
