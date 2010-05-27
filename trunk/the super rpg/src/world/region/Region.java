package world.region;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import world.World;
import world.modifier.Updateable;
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
	LinkedList<Updateable> u = new LinkedList<Updateable>();
	Semaphore uSem = new Semaphore(1, true);
	//LinkedList
	
	public Region()
	{
		
	}
	/**
	 * registers an object with the region
	 * @param unit
	 */
	public void registerObject(Unit unit)
	{
		try
		{
			uSem.acquire();
			u.add(unit);
			uSem.release();
		}
		catch(InterruptedException e){}
	}
	public void updateRegion(World w, double tdiff)
	{
		try
		{
			uSem.acquire();
			Iterator<Updateable> i = u.iterator();
			while(i.hasNext())
			{
				Updateable u = i.next();
				u.update(w, tdiff);
			}
			uSem.release();
		}
		catch(InterruptedException e){}
	}
	public void drawRegion(Graphics2D g, DisplayMode dm, Camera c)
	{
		g.setColor(Color.green);
		g.fillRect(0, 0, dm.getWidth(), dm.getHeight());
		/*for(Unit unit: u)
		{
			unit.draw(g);
		}*/
	}
}
