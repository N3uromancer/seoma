package world.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import world.networkUpdateable.NetworkUpdateable;

/**
 * defines a controller for taking user input and controlling game object,
 * controllers are registered and updated by the game world
 * @author Jack
 *
 */
public final class Controller implements KeyListener, MouseListener
{
	private HashSet<Character> down = new HashSet<Character>(); //keys that are currently depressed by the user
	private Semaphore downSem = new Semaphore(1, true);
	private HashMap<short[], Boolean> clicks = new HashMap<short[], Boolean>(); //true values indicate right clicks
	private Semaphore clickSem = new Semaphore(1, true);
	private Controllable c;
	
	/**
	 * creates a new controller that is associated with the passed id
	 * @param id
	 */
	public Controller(Controllable c)
	{
		this.c = c;
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
	public void updateController(NetworkUpdateable n, double tdiff)
	{
		try
		{
			downSem.acquire();
			clickSem.acquire();
			HashSet<Character> downTemp = new HashSet<Character>(down);
			HashMap<short[], Boolean> clickTemp = new HashMap<short[], Boolean>();
			downSem.release();
			clickSem.release();
			c.interpretUserInput(n, new UserInput(downTemp, clickTemp), tdiff);
		}
		catch(InterruptedException e){}
	}
	/**
	 * gets the object controlled by this controller
	 * @return returns the associated controllable object
	 */
	public Controllable getControlledObject()
	{
		return c;
	}
	public void mousePressed(MouseEvent e)
	{
		try
		{
			clickSem.acquire();
			clicks.put(new short[]{(short)e.getPoint().x, (short)e.getPoint().y}, e.getButton() == MouseEvent.BUTTON3);
			clickSem.release();
		}
		catch(InterruptedException a){}
	}
	public void mouseClicked(MouseEvent arg0){}
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}
	public void mouseReleased(MouseEvent arg0){}
}
