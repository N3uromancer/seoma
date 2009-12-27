package ai.aiModule;


import gameEngine.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import ai.AI;
import ui.userIO.userInput.KeyInput;
import ui.userIO.userInput.KeyPress;
import ui.userIO.userInput.KeyRelease;
import ui.userIO.userInput.UserInput;
import utilities.Camera;

/**
 * takes user input an automatically updates a camera
 * based off the input
 * @author Jack
 *
 */
public final class CameraModule implements AIModule
{
	private Camera c;
	private HashMap<Character, double[]> t = new HashMap<Character, double[]>(); //represents tranlational actions
	private HashMap<Character, Double> z = new HashMap<Character, Double>(); //represents zoom actions
	private HashSet<Character> down = new HashSet<Character>(); //keeps track of characters that are currently down
	
	/**
	 * creates a new camera module where the camera is updated based off the
	 * passed keys
	 * @param up
	 * @param right
	 * @param down
	 * @param left
	 * @param zoomIn
	 * @param zoomOut
	 */
	public CameraModule(char up, char right, char down, char left, char zoomIn, char zoomOut)
	{
		c = new Camera(0, 0);
		
		double m = 160; //movement distance
		t.put(up, new double[]{0, m});
		t.put(right, new double[]{m, 0});
		t.put(down, new double[]{0, -m});
		t.put(left, new double[]{-m, 0});
		
		m = 3; //zoom distance
		z.put(zoomIn, -m);
		z.put(zoomOut, m);
	}
	public void updateModule(AI ai, World w, HashMap<Class<? extends UserInput>, ArrayList<UserInput>> ui, double tdiff)
	{
		if(ui != null)
		{
			if(ui.containsKey(KeyPress.class))
			{
				ArrayList<UserInput> kpress = ui.get(KeyPress.class);
				Iterator<UserInput> kpi = kpress.iterator(); //key press iterator
				while(kpi.hasNext())
				{
					KeyInput ka = (KeyInput)kpi.next();
					down.add(ka.getCharacter());
					//System.out.println(ka.getCharacter()+" pressed");
				}
			}
			if(ui.containsKey(KeyRelease.class))
			{
				ArrayList<UserInput> krelease = ui.get(KeyRelease.class);
				Iterator<UserInput> kri = krelease.iterator(); //key release iterator
				while(kri.hasNext())
				{
					KeyInput ka = (KeyInput)kri.next();
					down.remove(ka.getCharacter());
					//System.out.println(ka.getCharacter()+" released");
				}
			}
		}
		
		if(down.size() > 0)
		{
			Iterator<Character> ci = down.iterator(); //character iterator
			while(ci.hasNext())
			{
				char key = ci.next();
				if(t.containsKey(key))
				{
					double[] translate = t.get(key);
					c.translate(translate[0]*tdiff, translate[1]*tdiff);
				}
				else if(z.containsKey(key))
				{
					double zoom = z.get(key);
					//c.zoom(zoom*tdiff);
				}
			}
		}
	}
	public Camera getCamera()
	{
		return c;
	}
}
