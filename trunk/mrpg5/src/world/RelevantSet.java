package world;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import network.IOConstants;
import world.modifier.GameObject;
import world.modifier.Locateable;
import world.region.Region;

/**
 * defines a relevant set of data associated with a specific game object,
 * the set can be updated and sent in pieces
 * @author Jack
 *
 */
public final class RelevantSet
{
	private short id;
	private Region region; //a ref to the region the set refers to
	private HashMap<GameObject, Integer> p = new HashMap<GameObject, Integer>(); //priorities
	private InetAddress address;
	
	private short update = Short.MIN_VALUE; //used as a time stamp when writing data to packets
	
	/**
	 * creates a new relevant set that pertains to an object
	 * @param id the id of the object the relevant set is associated with
	 * @param address the address the set should be sent to
	 */
	public RelevantSet(short id, InetAddress address)
	{
		this.id = id;
		this.address = address;
	}
	/**
	 * gets the id of the game object the set is associated with
	 * @return returns the id of the game object that the set is for
	 */
	public short getObjectID()
	{
		return id;
	}
	public void updateSet(World w)
	{
		Region r = w.getRegion(id);
		if(r != region)
		{
			//the object changed regions
			try
			{
				r.getSemaphore().acquire();
				for(GameObject o: r.getObjects())
				{
					p.put(o, o.getUpdatePriority());
				}
				r.getSemaphore().release();
				region = r;
			}
			catch(InterruptedException e){}
		}
		
		/*
		 * adjust priorities baseed off the update priorities for the 
		 * objects and their distance (if applicable) to the object for
		 * whom the relevant set is being compiled
		 */
		GameObject t = w.getObject(id);
		Locateable temp = null;
		if(t instanceof Locateable)
		{
			temp = (Locateable)t;
		}
		for(GameObject o: p.keySet())
		{
			if(temp != null && o instanceof Locateable)
			{
				Locateable l = (Locateable)o;
				int x = (int)(l.getLocation()[0]-temp.getLocation()[0]);
				int y = (int)(l.getLocation()[1]-temp.getLocation()[1]);
				int d = (int)Math.sqrt(x*x+y*y)/500;
				p.put(o, p.get(o)-d);
			}
			p.put(o, p.get(o)+o.getUpdatePriority());
		}
	}
	/**
	 * gets a byte array whose length is approximately equal to
	 * the passed max length that contains data relevant to the
	 * object that the relevant set is associated with
	 * 
	 * the data returned represents objects with the highest update priority
	 * based off a variety of factors
	 * @param maxLength
	 * @return returns a byte array containing the highest priority information
	 * of the relevant set
	 */
	public byte[] getRelevantData(int maxLength)
	{
		Comparator<GameObject> c = new Comparator<GameObject>()
		{
			public int compare(GameObject g1, GameObject g2)
			{
				return p.get(g1).compareTo(p.get(g2));
			}
		};
		int size = p.size();
		if(p.size() == 0)
		{
			size = 1;
		}
		PriorityQueue<GameObject> q = new PriorityQueue<GameObject>(size, c);
		q.addAll(p.keySet());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		byte count = Byte.MIN_VALUE;
		while(baos.size() < maxLength && q.size() > 0)
		{
			//polled items reset to their starting update priority
			GameObject g = q.poll();
			
			try
			{
				dos.writeShort(g.getID());
			}
			catch(IOException e){}
			
			g.writeState(dos);
			p.put(g, g.getUpdatePriority());
			count++;
		}
		byte[] buff = baos.toByteArray();
		baos = new ByteArrayOutputStream(buff.length+4);
		dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.updateWorld);
			dos.writeShort(update);
			dos.write(count);
			dos.write(buff);
		}
		catch(IOException e){}
		
		update++;
		return baos.toByteArray();
		/*
		 * does not return a packet so that byte arrays for other operations
		 * can be concatenated together
		 */
	}
	/**
	 * gets the address the data in the relevant set is to be sent to
	 * @return returns the client address that the relevant set is associated with
	 */
	public InetAddress getAddress()
	{
		return address;
	}
}
