package world;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.clientOperation.DestroyObject;
import network.operationExecutor.clientOperation.SpawnUnit;
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
public final class RelevantSet
{
	/**
	 * contains newly created objects whose spawn orders must be sent to the associated client
	 */
	private HashSet<NetworkUpdateable> newObjects = new HashSet<NetworkUpdateable>();
	private Semaphore newObjSem = new Semaphore(1, true);
	
	/**
	 * contains objects that have been destroyed on the server and have been created on the client,
	 * the client must be informed that the objects have been destroyed to maintain an accurate world state
	 */
	private LinkedList<NetworkUpdateable> destroyedObj = new LinkedList<NetworkUpdateable>();
	private Semaphore destObjSem = new Semaphore(1, true);

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
		System.out.println("relevent set created for id="+id+", connection="+c);
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
		if(destroyedObj.size() > 0 && c.getTCPQueueSize() < 20)
		{
			ArrayList<NetworkUpdateable> temp = new ArrayList<NetworkUpdateable>();
			try
			{
				destObjSem.acquire();
				//temp.addAll(destroyedObj);
				//destroyedObj = new ArrayList<NetworkUpdateable>();
				Iterator<NetworkUpdateable> it = destroyedObj.iterator();
				for(int i = 0; i < 256 && destroyedObj.size() > 0; i++) //for loop to cap destroy orders at 256
				{
					temp.add(it.next());
					it.remove();
				}
				destObjSem.release();
			}
			catch(InterruptedException e){}
			byte[] b = DestroyObject.createByteBuffer(temp, w);
			c.write(b, true);
		}
		if(newObjects.size() > 0 && c.getTCPQueueSize() < 20)
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
			System.out.println("rSet for id="+id+" detected relevant new objects, sending spawn orders to client");
			ArrayList<NetworkUpdateable> temp = new ArrayList<NetworkUpdateable>();
			try
			{
				newObjSem.acquire();
				//temp.addAll(newObjects);
				//newObjects = new HashSet<NetworkUpdateable>();
				Iterator<NetworkUpdateable> it = newObjects.iterator();
				for(int i = 0; i < 256 && newObjects.size() > 0; i++) //for loop to cap spawn orders at 256
				{
					temp.add(it.next());
					it.remove();
				}
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
				if(u.getID() != id && !u.isDead())
				{
					pSem.acquire();
					if(priorities.get(u) == null)
					{
						//detected a new object, adds the object to priority map and new object list
						priorities.put(u, u.getUpdatePriority());
						newObjSem.acquire();
						newObjects.add(u);
						newObjSem.release();
					}
					priorities.put(u, priorities.get(u)+u.getUpdatePriority());
					pSem.release();
				}
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
			int size = priorities.size() == 0? 1: priorities.size();
			PriorityQueue<NetworkUpdateable> q = new PriorityQueue<NetworkUpdateable>(size, c);
			q.addAll(priorities.keySet());
			pSem.release();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.write(IOConstants.updateNetworkObjects);
			dos.writeShort(updateIndex);
			updateIndex++;
			
			//System.out.println("========== update packet ==========");
			ByteArrayOutputStream netObjData = new ByteArrayOutputStream();
			DataOutputStream dos2 = new DataOutputStream(netObjData);
			byte length = Byte.MIN_VALUE;
			while(netObjData.size() < maxSize && q.size() > 0 && length < Byte.MAX_VALUE)
			{
				NetworkUpdateable n = q.poll();
				byte[] buff = n.getState();
				if(netObjData.size()+buff.length+3 <= maxSize)
				{
					length++;
					dos2.writeShort(n.getID());
					//System.out.println("id="+n.getID()+", "+n.getClass().getSimpleName());
					netObjData.write(buff.length-128);
					netObjData.write(buff);
				}
			}
			dos.write(length);
			dos.write(netObjData.toByteArray());
			return baos.toByteArray();
		}
		catch(InterruptedException e){}
		catch(IOException e){}
		return null;
	}
	/**
	 * called to notify the relevant set that an object has been destroyed, removes
	 * the object from the set priority map and cancels the object's spawn order if
	 * it has not already been sent, notifies the associated client, the client must
	 * be notified so that it can clear any outstanding data received regarding the
	 * object
	 * @param o
	 */
	public void destroyObject(NetworkUpdateable o)
	{
		try
		{
			destObjSem.acquire();
			destroyedObj.add(o);
			destObjSem.release();
			
			//semaphores must be acquired in this order to prevent deadlocking with udpateSet... method
			pSem.acquire();
			newObjSem.acquire();
			
			priorities.remove(o);
			newObjects.remove(o);
			
			newObjSem.release();
			pSem.release();
		}
		catch(InterruptedException e){}
	}
}
