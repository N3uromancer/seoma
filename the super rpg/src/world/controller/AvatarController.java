package world.controller;

import java.util.ArrayList;
import java.util.HashMap;

import world.attack.AttackInitializer;
import world.initializer.Initializable;
import world.networkUpdateable.NetworkUpdateable;
import world.unit.Unit;
import world.unit.action.MoveTowards;
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
	public ArrayList<Initializable> interpretUserInput(NetworkUpdateable n, UserInput ui, double tdiff)
	{
		ArrayList<Initializable> iniActions = new ArrayList<Initializable>();
		
		Unit a = (Unit)n;
		if(ui.getKeyPresses().size() > 0)
		{
			double[] t = new double[]{0, 0};
			for(char c: ui.getKeyPresses())
			{
				if(actions.containsKey(c))
				{
					//double[] l = a.getLocation();
					//double[] t = actions.get(c);
					//a.setLocation(new double[]{l[0]+t[0]*tdiff, l[1]+t[1]*tdiff});
					
					t[0]+=actions.get(c)[0];
					t[1]+=actions.get(c)[1];
				}
				if(z.containsKey(c))
				{
					zoom*=z.get(c);
				}
			}
			if(!(t[0]==0 && t[1]==0))
			{
				a.queueAction(new MoveTowards(t));
			}
		}
		if(ui.getMousePresses().size() > 0)
		{
			//System.out.println("mouse clicks detected in avatar controller");
			iniActions.add(new AttackInitializer(AttackInitializer.createAttack(n.getID(), (byte)0)));
		}
		location = a.getLocation();
		
		
		return iniActions;
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
