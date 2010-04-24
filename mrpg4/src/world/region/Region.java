package world.region;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import world.modifier.Drawable;
import world.modifier.GameObject;
import world.modifier.Permanent;
import world.modifier.Updateable;

/**
 * represents a piece of the game world
 * @author Jack
 *
 */
public class Region
{
	byte regionID;
	Semaphore s = new Semaphore(1, true); //controls access to the region
	HashMap<Short, GameObject> obj = new HashMap<Short, GameObject>();
	
	HashSet<Drawable> d = new HashSet<Drawable>();
	HashSet<Updateable> updateables = new HashSet<Updateable>();
	
	//PartitionManager pm;
	int width;
	int height;
	
	public Region()
	{
		regionID = 0;
		width = 1000;
		height = 700;
		
		//pm = new PartitionManager(0, 0, width, height, 30, 60, 50);
	}
	public void addObject(GameObject g)
	{
		if(!(g instanceof Permanent))
		{
			obj.put(g.getID(), g);
		}
		if(g instanceof Drawable)
		{
			d.add((Drawable)g);
		}
		if(g instanceof Updateable)
		{
			updateables.add((Updateable)g);
		}
	}
	public void updateRegion(double tdiff)
	{
		for(Updateable u: updateables)
		{
			u.update(tdiff);
		}
	}
	public void drawRegion(Graphics2D g, DisplayMode dm)
	{
		g.setColor(Color.green);
		g.fillRect(0, 0, dm.getWidth(), dm.getHeight());
		g.setColor(Color.black);
		g.drawRect(0, 0, width, height);
		
		/*try
		{
			s.acquire();
			
			s.release();
		}
		catch(InterruptedException e){}*/
		
		for(Drawable drawable: d)
		{
			drawable.draw(g, dm);
		}
	}
	public Semaphore getSemaphore()
	{
		return s;
	}
	/**
	 * gets the objects contained in the region
	 * @return returns a collection of objects contained in the region
	 */
	public Collection<GameObject> getObjects()
	{
		return obj.values();
	}
	public byte getID()
	{
		return regionID;
	}
	/**
	 * gets the game object referenced by the passed id
	 * @param id
	 * @return return a game object
	 */
	public GameObject getObject(int id)
	{
		return obj.get(id);
	}
}
