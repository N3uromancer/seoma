package world.region;

import geom.Boundable;
import geomUtil.PartitionManager;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import world.World;
import world.modifier.Drawable;
import world.networkUpdateable.NetworkUpdateable;
import display.Camera;

/**
 * defines a section of the game world, each region is specified in the world by
 * its byte id, each game object belongs to the world
 * @author Jack
 *
 */
public class Region
{
	LinkedList<NetworkUpdateable> u = new LinkedList<NetworkUpdateable>();
	HashMap<Short, NetworkUpdateable> uidMap = new HashMap<Short, NetworkUpdateable>(); //updateable id map
	HashSet<NetworkUpdateable> drawRemove = new HashSet<NetworkUpdateable>(); //contains ids of objects that are dead and need to be removed from the draw partition
	Semaphore uSem = new Semaphore(1, true); //controls access to both the linked list and the id map
	
	PartitionManager drawables;
	Semaphore dSem = new Semaphore(1, true);
	
	/**
	 * stores executed actions executed by the region, stored actions are retrieved by the world for processing
	 * by the relevant sets, stored actions should be cleared after they have been querried by the world, the key
	 * is the id of the network object that executed the action, the list contains groups of executed actions
	 * (necesary because the network object could be told to executed actions several times before the world itself
	 * querries the region for executed actions)
	 */
	HashMap<Short, ArrayList<byte[][]>> executedActions = new HashMap<Short, ArrayList<byte[][]>>();
	
	int[] dim = {1000, 1000}; //the dimensions of the region
	
	public Region()
	{
		drawables = new PartitionManager(0, 0, 1000, 1000, 20, 40, 400);
	}
	/**
	 * gets the network objects associated with this region, this method should
	 * always be used in conjunction with the get region semaphore method
	 * to ensure no concurrency problems
	 * @return returns a list of network updateable object registered with this region
	 */
	public LinkedList<NetworkUpdateable> getNetworkObjects()
	{
		return u;
	}
	public Semaphore getSemaphore()
	{
		return uSem;
	}
	public byte getRegionID()
	{
		return Byte.MIN_VALUE;
	}
	/**
	 * registers an object with the region
	 * @param o
	 */
	public void registerObject(NetworkUpdateable o)
	{
		try
		{
			uSem.acquire();
			u.add(o);
			uidMap.put(o.getID(), o);
			uSem.release();
			if(o instanceof Drawable)
			{
				dSem.acquire();
				drawables.add((Drawable)o);
				dSem.release();
			}
		}
		catch(InterruptedException e){}
	}
	/**
	 * updates an object contained within this region
	 * @param id the id of the object to be updated
	 * @param buff the information to be loaded by the object
	 */
	public void updateObject(short id, byte[] buff)
	{
		try
		{
			uSem.acquire();
			uidMap.get(id).loadState(buff);
			uSem.release();
		}
		catch(InterruptedException e){}
	}
	public void queueNetworkObjAction(short id, byte actionID, byte[] pertData)
	{
		try
		{
			uSem.acquire();
			uidMap.get(id).queueAction(actionID, pertData);
			uSem.release();
		}
		catch(InterruptedException e){}
	}
	/**
	 * gets a single network object from the region, this method should be used in conjunction
	 * to the get region semaphore method in order to assure no concurrency problems
	 * @param id the id of the network object to retrieve
	 * @return
	 */
	public NetworkUpdateable getNetworkObject(short id)
	{
		return uidMap.get(id);
	}
	/**
	 * returns the executed actions of the network objects in the region, executed actions
	 * are stored by the region until the clearExecutedAction method is called
	 * @return
	 */
	public HashMap<Short, ArrayList<byte[][]>> getExecutedActions()
	{
		return executedActions;
	}
	public void clearExecutedActions()
	{
		executedActions = new HashMap<Short, ArrayList<byte[][]>>();
	}
	/**
	 * updates the objects contained within the region
	 * @param w
	 * @param tdiff
	 */
	public void updateRegion(World w, double tdiff)
	{
		HashSet<NetworkUpdateable> destroy = new HashSet<NetworkUpdateable>(); //objects to be destroyed
		try
		{
			uSem.acquire();
			Iterator<NetworkUpdateable> i = u.iterator();
			while(i.hasNext())
			{
				NetworkUpdateable u = i.next();
				if(!u.isDead())
				{
					if(!u.isGhost())
					{
						u.update(w, tdiff);
					}
					else
					{
						u.simulate(w, tdiff);
					}
					if(executedActions.get(u.getID()) == null)
					{
						executedActions.put(u.getID(), new ArrayList<byte[][]>());
					}
					executedActions.get(u.getID()).add(u.executeActions(w));
				}
				else
				{
					destroy.add(u);
					i.remove();
				}
			}
			uSem.release();
		}
		catch(InterruptedException e){}
		if(destroy.size() > 0)
		{
			//destroyed objects removed after update to avoid deadlocking when calling the world destroy method
			for(NetworkUpdateable u: destroy)
			{
				w.destroyObject(u.getID());
				if(u instanceof Drawable)
				{
					drawRemove.add(u);
				}
				uidMap.remove(u.getID());
			}
		}
	}
	public void drawRegion(Graphics2D g, DisplayMode dm, Camera c)
	{
		g.setColor(Color.black);
		g.drawRect(0, 0, dim[0], dim[1]);
		g.fillRect(0, 0, 50, 50);
		
		try
		{
			HashSet<NetworkUpdateable> remove = null;
			if(drawRemove.size() > 0)
			{
				//checking the size in the line above might not be thread safe
				remove = new HashSet<NetworkUpdateable>();
				uSem.acquire();
				remove.addAll(drawRemove);
				drawRemove = new HashSet<NetworkUpdateable>();
				uSem.release();
			}
			
			dSem.acquire();
			if(remove != null)
			{
				for(NetworkUpdateable n: remove)
				{
					drawables.remove((Boundable)n);
				}
			}
			HashSet<Boundable> b = drawables.intersects(c.getViewBounds());
			dSem.release();
			for(Boundable temp: b)
			{
				((Drawable)temp).draw(g);
			}
		}
		catch(InterruptedException e){}
	}
}
