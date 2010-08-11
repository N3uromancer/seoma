package world.region;

import geom.Boundable;
import geomUtil.PartitionManager;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import world.World;
import world.modifier.Drawable;
import world.networkUpdateable.NetworkUpdateable;
import world.unit.Unit;
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
	
	int[] dim = {1000, 1000}; //the dimensions of the region
	
	public Region()
	{
		drawables = new PartitionManager(0, 0, 1000, 1000, 20, 100, 400);
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
	 * gets the units intersected by the passed boundable object
	 * @param b
	 * @return returns the units intersected by the passed boundable object
	 */
	public HashSet<Unit> getIntersectedUnits(Boundable b)
	{
		HashSet<Unit> units = new HashSet<Unit>();
		try
		{
			dSem.acquire();
			HashSet<Boundable> intersections = drawables.intersects(b);
			dSem.release();
			for(Boundable temp: intersections)
			{
				if(temp instanceof Unit)
				{
					units.add((Unit)temp);
				}
			}
			units.remove(b);
		}
		catch(InterruptedException e){}
		return units;
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
		catch(NullPointerException e)
		{
			//for debug purposes
			System.out.println(id+" cannot be updated, buff length = "+buff.length);
			e.printStackTrace();
		}
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
	 * gets the state for a single object contained within the region
	 * @param id
	 * @return returns the state of the object with the passed id
	 */
	public byte[] getState(short id)
	{
		byte[] state = null;
		try
		{
			uSem.acquire();
			state = uidMap.get(id).getState();
			uSem.release();
		}
		catch(InterruptedException e){}
		return state;
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
				if(((Drawable)temp).isDisplayed())
				{
					((Drawable)temp).draw(g);
				}
			}
		}
		catch(InterruptedException e){}
	}
}
