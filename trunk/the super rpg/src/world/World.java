package world;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import world.controller.Controller;
import world.modifier.NetworkUpdateable;
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
	private HashMap<Short, Region> objRegMap = new HashMap<Short, Region>(); //maps network objects to the region they are in
	
	private HashMap<Short, byte[]> objData = new HashMap<Short, byte[]>(); //stores data for objects not yet registered with the world
	private Semaphore objDataSem = new Semaphore(1, true);
	
	private HashMap<Short, NetworkUpdateable> waiting = new HashMap<Short, NetworkUpdateable>(); //contains objects that are waiting for their data
	private Semaphore waitingSem = new Semaphore(1, true);
	
	public World()
	{
		Region r = new Region();
		regions.put(r.getID(), r);
	}
	/**
	 * gets the region associated with the passed id
	 * @param id the id of the object
	 * @return returns the region containing the specified object
	 */
	public Region getAssociatedRegion(short id)
	{
		return objRegMap.get(id);
	}
	/**
	 * registers an object with the world, when this method is called the unit data map
	 * is quereied to determine if any outstanding data for the unit has already been received
	 * prior to the object's registration, if outstanding data has been received it is automtically
	 * loaded by the object
	 * @param regionID the region the object is to be placed in
	 * @param u the object to be registered
	 */
	public void registerObject(byte regionID, NetworkUpdateable u)
	{
		try
		{
			objDataSem.acquire();
			if(objData.get(u.getID()) != null)
			{
				//data already received before object spawn order
				u.loadState(objData.get(u.getID()));
				objData.remove(u.getID());
			}
			objDataSem.release();
		}
		catch(InterruptedException e){}
		objRegMap.put(u.getID(), regions.get(regionID));
		if(u.isReady())
		{
			//object ready for use within the world, registered with appropriate region
			regions.get(regionID).registerObject(u);
		}
		else
		{
			//object not ready, still waiting for data
			try
			{
				waitingSem.acquire();
				waiting.put(u.getID(), u);
				waitingSem.release();
			}
			catch(InterruptedException e){}
		}
	}
	/**
	 * updates a network object based off the passed arguments
	 * @param id the id of the object to be updated
	 * @param buff the byte buffer containing the information to be loaded
	 */
	public void updateNetworkObject(short id, byte[] buff)
	{
		if(objRegMap.get(id) != null)
		{
			//a spawn order for the object has alraedy been received
			if(waiting.containsKey(id))
			{
				//object not yet ready for use
				try
				{
					waitingSem.acquire();
					waiting.get(id).loadState(buff);
					if(waiting.get(id).isReady())
					{
						//object received data, registered with appropriate region
						objRegMap.get(id).registerObject(waiting.get(id));
						waiting.remove(id);
					}
					waitingSem.release();
				}
				catch(InterruptedException e){}
			}
			else
			{
				//object ready for use and already registered with a region
				objRegMap.get(id).updateObject(id, buff);
			}
		}
		else
		{
			//spawn order not received yet, stores the data instead
			try
			{
				objDataSem.acquire();
				objData.put(id, buff);
				objDataSem.release();
			}
			catch(InterruptedException e){}
		}
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
