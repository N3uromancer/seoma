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
import world.modifier.NetworkUpdateable;
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
	HashMap<Short, NetworkUpdateable> uidMap = new HashMap<Short, NetworkUpdateable>();
	Semaphore uSem = new Semaphore(1, true); //controls access to both the linked list and the id map
	
	PartitionManager drawables;
	Semaphore dSem = new Semaphore(1, true);
	
	public Region()
	{
		drawables = new PartitionManager(0, 0, 1000, 1000, 20, 40, 400);
	}
	public byte getID()
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
	/**
	 * updates the objects contained within the region
	 * @param w
	 * @param tdiff
	 */
	public void updateRegion(World w, double tdiff)
	{
		try
		{
			uSem.acquire();
			Iterator<NetworkUpdateable> i = u.iterator();
			while(i.hasNext())
			{
				NetworkUpdateable u = i.next();
				if(!u.isGhost())
				{
					u.update(w, tdiff);
				}
				else
				{
					u.simulate(w, tdiff);
				}
			}
			uSem.release();
		}
		catch(InterruptedException e){}
	}
	public void drawRegion(Graphics2D g, DisplayMode dm, Camera c)
	{
		g.setColor(Color.green);
		g.fillRect(0, 0, dm.getWidth(), dm.getHeight());
		try
		{
			dSem.acquire();
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
