package world.unit;

import java.util.HashMap;

import world.World;
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
	private HashMap<Character, double[]> actions;
	
	//camera objects
	private HashMap<Character, Double> z; //zoom key map
	private double zoom = 1;
	
	/**
	 * standard constructor for creating player avatar on non owning clients
	 * @param isGhost
	 * @param id
	 */
	public Avatar(boolean isGhost, short id)
	{
		super(isGhost, id, (byte)2, (short)15);
	}
	/**
	 * creates avatar for owning client and server, an avatar must be created this way
	 * for the owning client because it will not be updated automatically by a relevent set
	 * thusly never receiving its starting information
	 * @param isGhost
	 * @param id
	 * @param l
	 */
	public Avatar(boolean isGhost, short id, double[] l)
	{
		super(isGhost, id, (byte)2, l, (short)15);
		
		if(!isGhost)
		{
			//non ghost, client controlled unit
			double m = 160;
			actions = new HashMap<Character, double[]>();
			z = new HashMap<Character, Double>(); //zoom key map
			actions.put('w', new double[]{0, -m});
			actions.put('d', new double[]{m, 0});
			actions.put('s', new double[]{0, m});
			actions.put('a', new double[]{-m, 0});
			
			z.put('r', 1.1);
			z.put('f', .9);
		}
		
		setReady();
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
	public void update(World w, double tdiff)
	{
		
	}
}
