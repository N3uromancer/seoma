package world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import network.Connection;
import world.listener.SpawnListener;
import world.modifier.NetworkUpdateable;
import world.region.Region;

/**
 * maintains an accurate represenation of the relevent set of information for a
 * particular client, determines the information that must be sent to clients
 * in order to maintain their games
 * 
 * used by the game server
 * @author Jack
 *
 */
public final class RelevantSet implements SpawnListener
{
	HashMap<Region, HashSet<NetworkUpdateable>> newObjects = new HashMap<Region, HashSet<NetworkUpdateable>>();
	Semaphore newObjSem = new Semaphore(1, true);
	
	private short id;
	
	/**
	 * creates a new relevant set
	 * @param id the id of the client avatar the set is to be determined for
	 */
	public RelevantSet(short id)
	{
		this.id = id;
	}
	public void updateSet(World w, Connection c)
	{
		System.out.println("updating set...");
		Region r = w.getAssociatedRegion(id);
		if(newObjects.get(r) != null && c.getTCPQueueSize() < 20)
		{
			/*
			 * there should be something to watch the tcp queue size, if it
			 * is allowed to grow to big, useless information may be queued in
			 * front of more recent, pertinant information, a method should be
			 * devised to determine the rate at which the queue is growing or
			 * shrinking so that the max size can be adjusted dynamically
			 */
			System.out.println("client region contains new objects, sending spawn orders to client");
			ArrayList<NetworkUpdateable> temp = new ArrayList<NetworkUpdateable>();
			try
			{
				newObjSem.acquire();
				temp.addAll(newObjects.get(r));
				newObjects.remove(r);
				newObjSem.release();
			}
			catch(InterruptedException e){}
			
		}
	}
	public void objectSpawned(NetworkUpdateable o, Region r)
	{
		try
		{
			newObjSem.acquire();
			if(newObjects.get(r) == null)
			{
				newObjects.put(r, new HashSet<NetworkUpdateable>());
			}
			newObjects.get(r).add(o);
			newObjSem.release();
		}
		catch(InterruptedException e){}
	}
	public void objectDestroyed(NetworkUpdateable o, Region r)
	{
		try
		{
			newObjSem.acquire();
			if(newObjects.get(r) != null)
			{
				newObjects.get(r).remove(o);
			}
			newObjSem.release();
		}
		catch(InterruptedException e){}
	}
}
