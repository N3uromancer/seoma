package world;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import network.Operation;
import network.client.Client;
import network.client.output.ClientTCPWriterThread;
import network.client.output.ClientUDPWriterThread;

/**
 * represents the game world, each world has one associated
 * avatar that is passed the user input of the player
 * @author Jack
 *
 */
public class World implements KeyListener
{
	private HashMap<Long, GameObject> obj = new HashMap<Long, GameObject>();
	/**
	 * semaphore for access to the game object map, needed to ensure thread safe
	 * access by the update method and the client data receiver thread
	 */
	Semaphore objSem = new Semaphore(1, true);
	/**
	 * the id of the avatar associated with the world that the client
	 * player controls
	 */
	long avatarID;
	/**
	 * a set of keys that are currently depressed by the user
	 */
	HashSet<Character> down = new HashSet<Character>();
	/**
	 * semaphore to control access to the set containing depressed characters
	 */
	Semaphore downSem = new Semaphore(1, true);
	/**
	 * a map representing the user's actions by character
	 */
	HashMap<Character, double[]> actions = new HashMap<Character, double[]>();
	/**
	 * holds the value of the next id to be assigned upon request
	 */
	long id;
	/**
	 * the minimum values for ids assigned by this world
	 */
	long minID;
	/**
	 * the maximum values for ids assigned by this world
	 */
	long maxID;
	/**
	 * the group client in this world must join on their multicast socket
	 */
	String mcastGroup = "230.0.0.1";
	
	Client c;
	double time = 0;
	
	/**
	 * creates a new world
	 * @param idStart the starting id in the id range that the world has access to
	 * @param idEnd the ending id in the id range that the world has access to
	 */
	public World(long idStart, long idEnd, Client c)
	{
		this.c = c;
		
		double m = 100; //movement
		actions.put('w', new double[]{0, -m});
		actions.put('d', new double[]{m, 0});
		actions.put('s', new double[]{0, m});
		actions.put('a', new double[]{-m, 0});
		
		this.id = idStart;
		this.minID = idStart;
		this.maxID = idEnd;
		
		avatarID = getNewID();
		obj.put(avatarID, new GameObject(50, 50, false, avatarID));
	}
	/**
	 * gets the map of objects associated with this world
	 * @return
	 */
	public HashMap<Long, GameObject> getObjects()
	{
		return obj;
	}
	/**
	 * gets a new id to be assigned to a created game object, calling this method
	 * reservers the returned id for use, this method should not be called if
	 * a new id is not needed for a game object as it will waste ids
	 * @return returns an open id that can be assigned to a game object
	 */
	public long getNewID()
	{
		id++;
		return id-1;
	}
	/**
	 * draws the world
	 * @param g
	 * @param dm
	 */
	public void displayWorld(Graphics2D g, DisplayMode dm)
	{
		try
		{
			objSem.acquire();
			g.setColor(Color.green);
			g.fillRect(0, 0, dm.getWidth(), dm.getHeight());
			g.setColor(Color.red);
			for(Long l: obj.keySet())
			{
				obj.get(l).drawGameObject(g);
			}
			objSem.release();
		}
		catch(InterruptedException e){}
	}
	/**
	 * gets the semaphore controlling access to the game object map
	 * @return returns the game object semaphore
	 */
	public Semaphore getObjectSemaphore()
	{
		return objSem;
	}
	/**
	 * gets the mutlicast group for players in this world
	 * @return
	 */
	public String getMulticastGroup()
	{
		return mcastGroup;
	}
	/**
	 * updates the world
	 * @param tdiff the time difference to advance the world by
	 * @param tcp the writer thread that sends data to the server
	 */
	public void updateWorld(double tdiff, ClientTCPWriterThread tcp, ClientUDPWriterThread udp)
	{
		time+=tdiff;
		try
		{
			objSem.acquire();
			downSem.acquire();
			for(Character c: down)
			{
				if(actions.containsKey(c))
				{
					obj.get(avatarID).translate(actions.get(c), tdiff);
				}
			}
			downSem.release();
			
			//update world here
			
			
			objSem.release();

			for(int i = 0; i < 1; i++)
			{
				udp.add(obj.get(avatarID).getState(), obj.get(avatarID));
			}
		}
		catch(InterruptedException a){}
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
			c.disconnect();
		}
	}
}
