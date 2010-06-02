package world;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

import network.Connection;
import network.operationExecutor.clientOperation.SpawnUnit;
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
	private HashMap<Region, HashSet<NetworkUpdateable>> newObjects = new HashMap<Region, HashSet<NetworkUpdateable>>();
	private Semaphore newObjSem = new Semaphore(1, true);

	/**
	 * maintains a mapping of objects to their update priorities, the map only contains objects that are
	 * in the same region as the object for whom the relevant set is maintained, when the object for which
	 * the relevant set is maintained leaves a region the priority map is cleared
	 */
	private HashMap<NetworkUpdateable, Integer> priorities = new HashMap<NetworkUpdateable, Integer>();
	private Semaphore pSem = new Semaphore(1, true);
	
	private short id;
	private Connection c;
	private short updateIndex = Short.MIN_VALUE; //used to determine old messages, higher indeces are more recent (until it rolls over)
	
	/**
	 * creates a new relevant set
	 * @param id the id of the client avatar the set is to be determined for
	 * @param c the connection with which the relevant set is associated with, where it
	 * writes its information to
	 */
	public RelevantSet(short id, Connection c)
	{
		this.id = id;
		this.c = c;
	}
	/**
	 * updates the relevant set and sends the update information
	 * to the connection associated with the set
	 * @param w
	 */
	public void updateRelevantSet(World w)
	{
		//System.out.println("updating set...");
		Region r = w.getAssociatedRegion(id);
		if(newObjects.get(r) != null && c.getTCPQueueSize() < 20)
		{
			//there are spawn orders that need to be sent for the region
			/*
			 * there should be something to watch the tcp queue size, if it
			 * is allowed to grow to big, useless information may be queued in
			 * front of more recent, pertinant information, a method should be
			 * devised to determine the rate at which the queue is growing or
			 * shrinking so that the max size can be adjusted dynamically to
			 * maximize throughput
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
			byte[] b = SpawnUnit.createByteBuffer(temp, w);
			c.write(b, true);
		}
		
		//updates the priorities of all network objects
		try
		{
			r.getSemaphore().acquire();
			for(NetworkUpdateable u: r.getNetworkObjects())
			{
				pSem.acquire();
				if(priorities.get(u) == null)
				{
					priorities.put(u, u.getUpdatePriority());
				}
				priorities.put(u, priorities.get(u)+u.getUpdatePriority());
				pSem.release();
			}
			r.getSemaphore().release();
		}
		catch(InterruptedException e){}
		
		byte[] buff = compileClientUpdatePacket(100);
		c.write(buff, false);
	}
	/**
	 * compiles a byte buffer of information to be sent to the client
	 * that controls the unit for whom the relevant set is maintained,
	 * writes highest priority object state information to the buffer
	 * first before sending lesser priority information
	 * @param maxSize the maximum size of the byte buffer
	 * @return returns a byte buffer containing update information
	 * to be sent to the associated client
	 */
	private byte[] compileClientUpdatePacket(int maxSize)
	{
		try
		{
			pSem.acquire();
			Comparator<NetworkUpdateable> c = new Comparator<NetworkUpdateable>(){
				public int compare(NetworkUpdateable n1, NetworkUpdateable n2)
				{
					if(priorities.get(n1) > priorities.get(n2))
					{
						return -1;
					}
					else if(priorities.get(n1) < priorities.get(n2))
					{
						return 1;
					}
					return 0;
				}
			};
			PriorityQueue<NetworkUpdateable> q = new PriorityQueue<NetworkUpdateable>(priorities.size(), c);
			q.addAll(priorities.keySet());
			pSem.release();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeShort(updateIndex);
			updateIndex++;
			while(baos.size() < maxSize && q.size() > 0)
			{
				byte[] buff = q.poll().getState();
				if(baos.size()+buff.length <= maxSize)
				{
					baos.write(buff);
				}
			}
			return baos.toByteArray();
		}
		catch(InterruptedException e){}
		catch(IOException e){}
		return null;
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
			
			//network updateable is removed from the update map if necesary
			pSem.acquire();
			priorities.remove(o);
			pSem.release();
		}
		catch(InterruptedException e){}
	}
}
