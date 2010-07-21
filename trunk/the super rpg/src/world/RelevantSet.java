package world;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.clientOperation.DestroyObject;
import network.operationExecutor.jointOperation.PerformInitialization;
import world.initializer.Initializable;
import world.networkUpdateable.NetworkUpdateable;
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
	
	private LinkedList<Initializable> ini = new LinkedList<Initializable>(); //initializations that have occured on the server
	private HashMap<Initializable, byte[]> iniData = new HashMap<Initializable, byte[]>();
	private Semaphore iniSem = new Semaphore(1, true);
	
	private short id; //the id of the unit for whom the relevant set is maintained
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
		if(destroyedObj.size() > 0 && c.getTCPQueueSize() < 40)
		{
			ArrayList<NetworkUpdateable> temp = new ArrayList<NetworkUpdateable>();
			try
			{
				destObjSem.acquire();
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
		if(ini.size() > 0 && c.getTCPQueueSize() < 40)
		{
			/*
			 * there should be something to watch the tcp queue size, if it
			 * is allowed to grow to big, useless information may be queued in
			 * front of more recent, pertinant information, a method should be
			 * devised to determine the rate at which the queue is growing or
			 * shrinking so that the max size can be adjusted dynamically to
			 * maximize throughput
			 */
			System.out.println("rSet for id="+id+" detected initialization orders");
			HashMap<Initializable, byte[]> temp = new HashMap<Initializable, byte[]>();
			try
			{
				iniSem.acquire();
				Iterator<Initializable> it = ini.iterator();
				for(int i = 0; i < 256 && ini.size() > 0; i++) //for loop to cap spawn orders at 256
				{
					Initializable n = it.next();
					if(n.immediatelyRelevant(id, w))
					{
						//order relevant to client, sent
						temp.put(n, iniData.get(n));
						iniData.remove(n);
						it.remove();
					}
					else if(!n.isRelevant(id, w))
					{
						//not relevant to client, initialization order not sent
						iniData.remove(n);
						it.remove();
					}
				}
				iniSem.release();
			}
			catch(InterruptedException e){}
			byte[] b = PerformInitialization.createByteBuffer(temp, w);
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
					if(priorities.get(u) == null && u.getUpdatePriority() > 0)
					{
						//detected a new object, adds the object to priority map
						priorities.put(u, u.getUpdatePriority());
					}
					else
					{
						priorities.put(u, priorities.get(u)+u.getUpdatePriority());
					}
					pSem.release();
				}
			}
			r.getSemaphore().release();
		}
		catch(InterruptedException e){}
		
		byte[] buff = compileClientUpdatePacket(300);
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
					priorities.put(n, n.getUpdatePriority());
				}
			}
			dos.write(length);
			dos.write(netObjData.toByteArray());
			pSem.release();
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
			if(o.broadcastDeath())
			{
				//clients require notification of object destruction
				destObjSem.acquire();
				destroyedObj.add(o);
				destObjSem.release();
			}
			
			//semaphores must be acquired in this order to prevent deadlocking with udpateSet... method
			pSem.acquire();
			priorities.remove(o);
			pSem.release();
		}
		catch(InterruptedException e){}
	}
	/**
	 * notifies the relevant set that an initialization action has occured
	 * @param i
	 * @param args
	 */
	public void initializationPerformed(Initializable i, byte[] args)
	{
		try
		{
			iniSem.acquire();
			ini.add(i);
			iniData.put(i, args);
			iniSem.release();
		}
		catch(InterruptedException e){}
	}
}
