package world.controller;

import java.util.HashMap;

import world.networkUpdateable.NetworkUpdateable;
import world.unit.Unit;
import display.Camera;

public final class AvatarController implements Controllable
{
	private short id;
	private HashMap<Character, double[]> actions;
	private HashMap<Character, Double> z; //zoom key map
	private double zoom = 1;
	
	private double[] location;
	
	public AvatarController(short id)
	{
		this.id = id;
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
	public void interpretUserInput(NetworkUpdateable n, UserInput ui, double tdiff)
	{
		Unit a = (Unit)n;
		for(char c: ui.getKeyPresses())
		{
			if(actions.containsKey(c))
			{
				double[] l = a.getLocation();
				double[] t = actions.get(c);
				a.setLocation(new double[]{l[0]+t[0]*tdiff, l[1]+t[1]*tdiff});
			}
			if(z.containsKey(c))
			{
				zoom*=z.get(c);
			}
		}
		if(ui.getMousePresses().size() > 0)
		{
			System.out.println("mouse clicked");
		}
		location = a.getLocation();
	}
	public void adjustCamera(Camera c)
	{
		if(location != null)
		{
			double[] l = {location[0], location[1]};
			l[1]*=-1;
			c.centerCamera(l);
			c.zoom(zoom);
		}
	}
	public short getControlledObjID()
	{
		return id;
	}
}
