package world.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import world.modifier.GameObject;
import world.modifier.Movable;
import world.modifier.Permanent;

/**
 * defines a controller for taking user input and controlling
 * a non-permanent game object, controllers are registered with
 * a world and updated by the world
 * @author Jack
 *
 */
public class Controller implements KeyListener
{
	HashSet<Character> down = new HashSet<Character>(); //keys that are currently depressed by the user
	Semaphore downSem = new Semaphore(1, true);
	HashMap<Character, double[]> actions = new HashMap<Character, double[]>();
	GameObject g;
	/**
	 * creates a new controller that is associated with the passed id
	 * @param id
	 */
	public Controller(GameObject g)
	{
		if(g instanceof Permanent)
		{
			System.err.println("controller cannot control a permanent game object");
			System.exit(0);
		}
		
		this.g = g;
		if(g instanceof Movable)
		{
			double m = ((Movable)g).getMovement();
			actions.put('w', new double[]{0, -m});
			actions.put('d', new double[]{m, 0});
			actions.put('s', new double[]{0, m});
			actions.put('a', new double[]{-m, 0});
		}
	}
	public void keyPressed(KeyEvent e)
	{
		if(!down.contains(e.getKeyChar()))
		{
			try
			{
				downSem.acquire();
				down.add(e.getKeyChar());
				downSem.release();
			}
			catch(InterruptedException a){}
		}
	}
	public void keyReleased(KeyEvent e)
	{
		try
		{
			downSem.acquire();
			down.remove(e.getKeyChar());
			downSem.release();
		}
		catch(InterruptedException a){}
	}
	public void keyTyped(KeyEvent e)
	{
		System.out.println("here");
		if(e.getKeyChar() == KeyEvent.VK_ESCAPE)
		{
			System.exit(0);
		}
	}
	/**
	 * updates the object associated with the controller based
	 * off registered user input
	 * @param tdiff
	 */
	public void updateController(double tdiff)
	{
		if(g instanceof Movable)
		{
			try
			{
				downSem.acquire();
				for(Character c: down)
				{
					if(actions.containsKey(c))
					{
						double[] l = ((Movable)g).getLocation();
						l[0]+=actions.get(c)[0]*tdiff;
						l[1]+=actions.get(c)[1]*tdiff;
						((Movable)g).setLocation(l);
					}
				}
				downSem.release();
			}
			catch(InterruptedException e){}
		}
	}
	/**
	 * gets the object controlled by this controller
	 * @return returns the associated controlled object
	 */
	public GameObject getControlledObject()
	{
		return g;
	}
}
