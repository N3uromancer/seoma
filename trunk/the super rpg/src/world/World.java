package world;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.util.HashMap;

import world.controller.Controller;
import world.modifier.Updateable;
import world.region.Region;
import display.Camera;

/**
 * definse the entirety of the game world, the world is divided into regions
 * @author Jack
 *
 */
public final class World
{
	private short id = Short.MIN_VALUE;
	private Controller c;
	private Camera camera;
	
	private HashMap<Byte, Region> regions = new HashMap<Byte, Region>(); //maps regions to their ids
	private HashMap<Short, Region> units = new HashMap<Short, Region>(); //maps units to the region they are in
	
	public World()
	{
		Region r = new Region();
		regions.put(Byte.MIN_VALUE, r);
	}
	/**
	 * gets the region associated with the passed id
	 * @param id the id of the object
	 * @return returns the region containing the specified object
	 */
	public Region getAssociatedRegion(short id)
	{
		return units.get(id);
	}
	public void registerObject(byte region, Updateable u)
	{
		regions.get(region).registerUnit(unit);
	}
	public void drawWorld(Graphics2D g, DisplayMode dm)
	{
		if(c != null)
		{
			c.getControlledObject().adjustCamera(camera);
			g.setTransform(camera.getTransform());
			
			
		}
	}
	public void updateWorld(double tdiff)
	{
		if(c != null)
		{
			c.updateController(tdiff);
		}
	}
	/**
	 * generates an unused id for a game object
	 * @return returns a new id
	 */
	public synchronized short generateNewID()
	{
		id++;
		return (short)(id-1);
	}
	/**
	 * sets the controller to be used and updated by the world
	 * @param c
	 */
	public void setController(Controller c, DisplayMode dm)
	{
		this.c = c;
		camera = new Camera(new double[]{0, 0}, dm.getWidth(), dm.getHeight());
		System.out.println("controller registered with world, new camera created");
	}
}
