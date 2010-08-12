package world;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
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
	
	private LinkedList<Initializable> ini = new LinkedList<Initializable>(); //queued initializations to be sent to the connected client
	
	private short id; //the id of the unit for whom the relevant set is maintained
	private Set<Short> sentIDs = Collections.synchronizedSet(new HashSet<Short>());
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
		sentIDs.add(id);
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
		sendDestructions(w);
		updatePriorities(w);
		sendInitializations(w);
		byte[] buff = compileClientUpdatePacket(300);
		c.write(buff, false);
	}
	/**
	 * notifies the associated client of destroyed network objects
	 * @param w
	 */
	private void sendDestructions(World w)
	{
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
	}
	/**
	 * updates the priority of all relevant network objects and detects new
	 * relevant network objects, if new objects are detected then their
	 * initialization orders are traced and flagged to be sent to the client
	 * @param w
	 */
	private void updatePriorities(World w)
	{
		Region r = w.getAssociatedRegion(id);
		try
		{
			r.getSemaphore().acquire();
			for(NetworkUpdateable u: r.getNetworkObjects())
			{
				if(u.getID() != id && !u.isDead())
				{
					if(u.isRelevant(id, this, w))
					{
						pSem.acquire();
						if(priorities.get(u) == null)
						{
							//detected a new object, adds the object to priority map
							priorities.put(u, u.getUpdatePriority());
							if(!sentIDs.contains(u.getID()) && !initializationOrderSent(u.getID(), w))
							{
								ini.add(w.traceInitialization(u.getID()));
								sentIDs.add(u.getID());
							}
						}
						else
						{
							priorities.put(u, priorities.get(u)+u.getUpdatePriority());
						}
						pSem.release();
					}
					else
					{
						pSem.acquire();
						priorities.remove(u);
						pSem.release();
					}
				}
			}
			r.getSemaphore().release();
		}
		catch(InterruptedException e){}
	}
	/**
	 * sends the queued initialization orders to the assoicated client
	 * @param w
	 */
	private void sendInitializations(World w)
	{
		if(ini.size() > 0 && c.getTCPQueueSize() < 60)
		{
			/*
			 * there should be something to watch the tcp queue size, if it
			 * is allowed to grow to big, useless information may be queued in
			 * front of more recent, pertinant information, a method should be
			 * devised to determine the rate at which the queue is growing or
			 * shrinking so that the max size can be adjusted dynamically to
			 * maximize throughput
			 */
			/*System.out.println("rSet for id="+id+" detected initialization orders");
			HashMap<Initializable, byte[]> temp = new HashMap<Initializable, byte[]>();
			Iterator<Initializable> it = ini.iterator();
			for(int i = 0; i < 256 && ini.size() > 0; i++) //for loop to cap spawn orders at 256
			{
				Initializable n = it.next();
				temp.put(n, n.getIniArgs());
				it.remove();
			}
			byte[] b = PerformInitialization.createByteBuffer(temp, w);
			c.write(b, true);*/
			
			System.out.println("rSet for id="+id+" detected initialization orders");
			ArrayList<Initializable> temp = new ArrayList<Initializable>();
			Iterator<Initializable> it = ini.iterator();
			for(int i = 0; i < 256 && ini.size() > 0; i++) //for loop to cap spawn orders at 256
			{
				Initializable ini = it.next();
				temp.add(ini);
				//System.out.println(ini.getClass().getSimpleName()+" sent");
				it.remove();
			}
			byte[] b = PerformInitialization.createByteBuffer(temp, w, false);
			c.write(b, true);
		}
	}
	/**
	 * compiles a byte buffer of information to be sent to the client
	 * that controls the unit for whom the relevant set is maintained,
	 * writes highest priority object state information to the buffer
	 * first before sending lesser priority information
	 * 
	 * objects whose update priority is less than 1, or whose state
	 * information is null are not sent
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
				if(buff != null && netObjData.size()+buff.length+3 <= maxSize && n.getUpdatePriority() > 0)
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
	 * tests to determine if the initialization order for the passed id has
	 * already been sent to the associated client
	 * @param id
	 * @param w
	 * @return returns true if the initialization order has already been sent,
	 * false otherwise
	 */
	private boolean initializationOrderSent(short id, World w)
	{
		Initializable i = w.traceInitialization(id);
		HashSet<Short> createdObjIDs = w.getInitializedObjects(i);
		for(Short objID: createdObjIDs)
		{
			if(sentIDs.contains(objID))
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * called to notify the relevant set that an object has been destroyed, removes
	 * the object from the set priority map and cancels the object's spawn order if
	 * it has not already been sent, notifies the associated client, the client must
	 * be notified so that it can clear any outstanding data received regarding the
	 * object
	 * @param o
	 * @param w
	 */
	public void destroyObject(NetworkUpdateable o, World w)
	{
		try
		{
			if(o.broadcastDeath() && initializationOrderSent(o.getID(), w))
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
	public boolean isSent(short id)
	{
		return sentIDs.contains(id);
	}
}
