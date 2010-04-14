package world;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;

import world.controller.Controller;
import world.modifier.GameObject;
import world.modifier.Permanent;
import world.region.Region;
import world.unit.Avatar;
import world.unit.StillUnit;

/**
 * encompasses the entirety of the game world, calling methods
 * of the world are thread safe
 * 
 * all things modifying the state of the world or objects registered
 * in the world should use the methods of the world
 * @author Jack
 *
 */
public final class World
{
	private HashMap<Byte, Region> regions = new HashMap<Byte, Region>();
	private HashMap<Short, Region> objRegMap= new HashMap<Short, Region>(); //mapping for the regions game objects are in
	private HashMap<Short, GameObject> obj = new HashMap<Short, GameObject>(); //mapping of game objects by id
	private Controller c; //controller for an object in the world, only for clients, null for server
	
	private HashSet<Short> avatars = new HashSet<Short>(); //set of player avatar ids
	
	short newID = 0;
	
	/**
	 * creates a new blank world
	 */
	public World()
	{
		Region e = new Region();
		regions.put(e.getID(), e);
		
		for(int i = 0; i < 10 && false; i++)
		{
			registerObject(new StillUnit(new double[]{Math.random()*100, Math.random()*300}), (byte)0);
		}
	}
	/**
	 * registers a game object with the world, automatically assigns
	 * an id to the registered object, meant for server side
	 * @param g
	 * @param region
	 */
	public void registerObject(GameObject g, byte region)
	{
		registerObject(g, newID++, region);
	}
	/**
	 * registers an object with the game world, uses the passed id as
	 * the reference id for the object, meant to be used client side
	 * @param g
	 * @param id
	 * @param region
	 */
	public void registerObject(GameObject g, short id, byte region)
	{
		if(!(g instanceof Permanent))
		{
			g.setID(id);
			if(g instanceof Avatar)
			{
				avatars.add(g.getID());
				System.out.println("avatar registered with the world, id="+g.getID());
			}
			obj.put(g.getID(), g);
			objRegMap.put(g.getID(), regions.get(region));
		}
		regions.get(region).addObject(g);
	}
	/**
	 * gets the region that contains the object with the passed id
	 * @param id
	 * @return returns a region containing the object with the passed id
	 */
	public Region getRegion(short id)
	{
		return objRegMap.get(id);
	}
	/**
	 * sets the controller associated with this world,
	 * a world can only have one controller associated with it,
	 * each controller must have a non-null associated object
	 * @param c
	 */
	public void setController(Controller c)
	{
		if(c.getControlledObject() != null)
		{
			this.c = c;
		}
		else
		{
			System.err.println("null object for the controller");
			System.exit(0);
		}
	}
	public Controller getController()
	{
		return c;
	}
	/**
	 * gets the game object specified by the passed id
	 * @param id
	 * @return returns the game object specified by the passed id
	 */
	public GameObject getObject(short id)
	{
		return obj.get(id);
	}
	public void drawWorld(Graphics2D g, DisplayMode dm)
	{
		if(c != null)
		{
			//long start = System.currentTimeMillis();
			//Avatar a = (Avatar)c.getControlledObject();
			
			objRegMap.get(c.getControlledObject().getID()).drawRegion(g, dm);
			
			//a.draw(g, dm);
			//System.out.println(System.currentTimeMillis()-start);
		}
	}
	public void updateWorld(double tdiff)
	{
		//updates any region containing avatars
		for(short s: avatars)
		{
			objRegMap.get(s).updateRegion(tdiff);
		}
	}
}