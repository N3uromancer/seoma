package world.unit;

import java.util.HashMap;

import world.controller.Controllable;
import world.controller.UserInput;
import display.Camera;

/**
 * defines a user controlled avatar, functions like a normal unit except it is controllable
 * @author Jack
 *
 */
public class Avatar extends Unit implements Controllable
{
	private HashMap<Character, double[]> actions = new HashMap<Character, double[]>();
	
	//camera objects
	private HashMap<Character, Double> z = new HashMap<Character, Double>(); //zoom key map
	private double zoom = 1;
	
	public Avatar(boolean isGhost)
	{
		super(isGhost);
		
		double m = 100;
		actions.put('w', new double[]{0, -m});
		actions.put('d', new double[]{m, 0});
		actions.put('s', new double[]{0, m});
		actions.put('a', new double[]{-m, 0});
		
		z.put('r', 1.1);
		z.put('f', .9);
	}
	public void interpretUserInput(UserInput ui, double tdiff)
	{
		for(char c: ui.getKeyPresses())
		{
			if(actions.containsKey(c))
			{
				double[] l = getLocation();
				double[] t = actions.get(c);
				setLocation(new double[]{l[0]+t[0]*tdiff, l[1]+t[1]*tdiff});
			}
			if(z.containsKey(c))
			{
				zoom*=z.get(c);
			}
		}
	}
	public void adjustCamera(Camera c)
	{
		c.zoom(zoom);
	}
}
