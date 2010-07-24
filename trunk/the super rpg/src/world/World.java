package world;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import network.Connection;
import world.controller.Controller;
import world.initializer.Initializable;
import world.initializer.Initializer;
import world.networkUpdateable.NetworkUpdateable;
import world.region.Region;
import world.unit.Avatar;
import world.unit.UnitInitializer;
import display.Camera;

/**
 * defines the entirety of the game world, the world is divided into regions
 * @author Jack
 *
 */
public final class World
{
	private short id = Short.MIN_VALUE; //the current id to be returned when a new id is requested, ids are useable once
	
	private Controller c;
	public static final int gridSize = 50; //grid size used for terrain in regions
	
	private Initializer initializer;
	private LinkedBlockingQueue<Initializable> initializations = new LinkedBlockingQueue<Initializable>(); //queued initialize actions
	private Semaphore iniSem = new Semaphore(1, true);
	
	/**
	 * maps each network updateable id in the world to the initializable that created it
	 */
	private Map<Short, Initializable> objIniMap = Collections.synchronizedMap(new HashMap<Short, Initializable>());
	
	private HashMap<Byte, Region> regions = new HashMap<Byte, Region>(); //maps regions to their ids
	/**
	 * maps network objects to the region they are in, all objects are mapped when registered with the world
	 * regardless of their ready status
	 */
	private HashMap<Short, Region> objRegMap = new HashMap<Short, Region>();
	
	private HashMap<Short, byte[]> objData = new HashMap<Short, byte[]>(); //stores data for objects not yet registered with the world
	private Semaphore objDataSem = new Semaphore(1, true);
	
	private HashMap<Short, NetworkUpdateable> waiting = new HashMap<Short, NetworkUpdateable>(); //contains objects that are waiting for their data
	private Semaphore waitingSem = new Semaphore(1, true);
	
	private ArrayList<RelevantSet> relevantSets = new ArrayList<RelevantSet>();
	private Semaphore relSetSem = new Semaphore(1, true);
	
	/**
	 * contains the ids of all avatars in the game world, only regions that contain an avatar
	 * are updated by the world
	 */
	private HashSet<Short> avatars = new HashSet<Short>();
	private Semaphore avatarSem = new Semaphore(1, true);
	
	public World()
	{
		Region r = new Region();
		regions.put(r.getRegionID(), r);
		
		initializer = new Initializer(new String[]{UnitInitializer.class.getName()});
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
	 * destroys the passed object, removes the object from the game world
	 * and notifies clients to destroy the object, declares the object dead
	 * @param id the id of the object to be destroyed
	 */
	public void destroyObject(short id)
	{
		try
		{
			objIniMap.remove(id);
			
			Region r = objRegMap.get(id);
			r.getSemaphore().acquire();
			NetworkUpdateable u = r.getNetworkObject(id);
			r.getSemaphore().release();
			u.setDead();
			
			relSetSem.acquire();
			for(RelevantSet set: relevantSets)
			{
				set.destroyObject(u);
			}
			relSetSem.release();
			
			if(u instanceof Avatar)
			{
				avatarSem.acquire();
				avatars.remove(u.getID());
				avatarSem.release();
			}
			
			objDataSem.acquire();
			objData.remove(id);
			objDataSem.release();
			
			waitingSem.acquire();
			waiting.remove(u);
			waitingSem.release();
		}
		catch(InterruptedException e){}
	}
	/**
	 * traces the initialization order for the passed id
	 * @param id
	 * @return returns the initializable object that created the
	 * network updateable with the passed id
	 */
	public Initializable traceInitialization(short id)
	{
		return objIniMap.get(id);
	}
	/**
	 * registers an object with the world, when this method is called the unit data map
	 * is quereied to determine if any outstanding data for the unit has already been received
	 * prior to the object's registration, if outstanding data has been received it is automtically
	 * loaded by the object, this method should only be called by initializables to properly guarantee
	 * that the action of initializing the object is relayed to each connected client
	 * @param regionID the region the object is to be placed in
	 * @param u the object to be registered
	 * @param i the initializable that created the object
	 */
	private void registerObject(byte regionID, NetworkUpdateable u, Initializable i)
	{
		objIniMap.put(u.getID(), i);
		System.out.println("registering object id="+u.getID()+"...");
		if(u.isGhost())
		{
			//only ghost objects would have received information
			try
			{
				objDataSem.acquire();
				if(objData.get(u.getID()) != null)
				{
					//data already received before object spawn order
					System.out.println("loading pre-received data...");
					u.loadState(objData.get(u.getID()));
					objData.remove(u.getID());
				}
				objDataSem.release();
			}
			catch(InterruptedException e){}
		}
		objRegMap.put(u.getID(), regions.get(regionID));
		if(u.isReady())
		{
			//object ready for use within the world, registered with appropriate region
			System.out.println("object ready, registered with region "+regionID);
			//regions.get(regionID).registerObject(u);
			spawnObject(u, regions.get(regionID));
		}
		else
		{
			//object not ready, still waiting for data
			System.out.println("object not ready, waiting for data");
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
	 * called to properly register an object with a specific region
	 * @param u
	 * @param r
	 */
	private void spawnObject(NetworkUpdateable u, Region r)
	{
		r.registerObject(u);
		if(u instanceof Avatar)
		{
			try
			{
				avatarSem.acquire();
				avatars.add(u.getID());
				avatarSem.release();
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
				System.out.println("id="+id+" was waiting, data received, loading data and adding to region");
				//object not yet ready for use
				try
				{
					waitingSem.acquire();
					waiting.get(id).loadState(buff);
					if(waiting.get(id).isReady())
					{
						//object received data, registered with appropriate region
						//objRegMap.get(id).registerObject(waiting.get(id));
						spawnObject(waiting.get(id), objRegMap.get(id));
						waiting.remove(id);
					}
					waitingSem.release();
				}
				catch(InterruptedException e){}
			}
			else
			{
				//System.out.println("updating id="+id);
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
			g.setColor(Color.green);
			g.fillRect(0, 0, dm.getWidth(), dm.getHeight());
			
			Camera camera = new Camera(new double[]{0, 0}, dm.getWidth(), dm.getHeight());
			c.getControlledObject().adjustCamera(camera);
			g.setTransform(camera.getTransform());
			Region r = objRegMap.get(((NetworkUpdateable)c.getControlledObject()).getID());
			r.drawRegion(g, dm, camera);
		}
	}
	public void updateWorld(double tdiff)
	{
		if(c != null)
		{
			c.updateController(tdiff);
		}
		try
		{
			HashSet<Region> updatedRegions = new HashSet<Region>();
			avatarSem.acquire();
			for(short id: avatars)
			{
				if(!updatedRegions.contains(objRegMap.get(id)))
				{
					objRegMap.get(id).updateRegion(this, tdiff);
					updatedRegions.add(objRegMap.get(id));
				}
			}
			avatarSem.release();
			
			while(initializations.size() > 0)
			{
				iniSem.acquire();
				Initializable i = initializations.poll();
				iniSem.release();
				//avoid deadlocking threads if the initializable initializes another initializable
				HashMap<NetworkUpdateable, Byte> netObj = i.initialize(this);
				for(NetworkUpdateable u: netObj.keySet())
				{
					registerObject(netObj.get(u), u, i);
				}
			}
			
			relSetSem.acquire();
			for(RelevantSet r: relevantSets)
			{
				r.updateRelevantSet(this);
			}
			relSetSem.release();
		}
		catch(InterruptedException e){}
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
	public void setController(Controller c)
	{
		this.c = c;
		System.out.println("controller registered with world");
	}
	/**
	 * registers a relevant set to be maintained by the world
	 * @param id the id of the object for which the set is to be maintained
	 * @param c the connection that the set is to write its udpate messages to
	 */
	public void registerRelevantSet(short id, Connection c)
	{
		try
		{
			relSetSem.acquire();
			relevantSets.add(new RelevantSet(id, c));
			relSetSem.release();
		}
		catch(InterruptedException e){}
	}
	public Controller getController()
	{
		return c;
	}
	/**
	 * adds an initializate action to the initialize queue, actions are deffered until
	 * next world update call
	 * @param i
	 * @param args
	 */
	public void initialize(Initializable i)
	{
		try
		{
			iniSem.acquire();
			initializations.add(i);
			iniSem.release();
		}
		catch(InterruptedException e){}
	}
	public Initializer getInitializer()
	{
		return initializer;
	}
}
