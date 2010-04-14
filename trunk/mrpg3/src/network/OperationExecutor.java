package network;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import world.World;

/**
 * executes operations of received packets
 * @author Jack
 *
 */
public class OperationExecutor implements Runnable
{
	LinkedBlockingQueue<DatagramPacket> q = new LinkedBlockingQueue<DatagramPacket>();
	Semaphore s = new Semaphore(1, true);
	World w;
	HashMap<String, Short> updateCounts = new HashMap<String, Short>();
	
	public OperationExecutor(World w)
	{
		this.w = w;
		new Thread(this).start();
	}
	public void run()
	{
		for(;;)
		{
			while(q.size() > 0)
			{
				try
				{
					s.acquire();
					DatagramPacket packet = q.poll();
					s.release();
					
					String hostIP = packet.getAddress().getHostAddress();
					if(!updateCounts.containsKey(hostIP))
					{
						updateCounts.put(hostIP, Short.MIN_VALUE);
					}
					ByteBuffer buff = ByteBuffer.wrap(packet.getData());
					
					short updateCount = buff.getShort();
					if(updateCount > updateCounts.get(hostIP))
					{
						//most recent packet
						updateCounts.put(hostIP, updateCount);
						
						byte regionID = buff.get(); //the id of the region this packet refers to
						
						w.getAvatar(hostIP).readState(buff); //it should be thread safe
						
						byte length = buff.get();
						int id;
						w.getRegion(regionID).getSemaphore().acquire();
						for(byte i = Byte.MIN_VALUE; i < length; i++)
						{
							id = buff.getInt();
							w.getRegion(regionID).getObject(id).readState(buff);
						}
						w.getRegion(regionID).getSemaphore().release();
					}
				}
				catch(InterruptedException e){}
			}
			synchronized(this)
			{
				try
				{
					wait();
				}
				catch(InterruptedException e){}
			}
		}
	}
	/**
	 * adds the passed packet to the queue to be executed
	 * and notifies the execution thread to start
	 * @param packet
	 */
	public synchronized void add(DatagramPacket packet)
	{
		try
		{
			s.acquire();
			q.add(packet);
			s.release();
			notify();
		}
		catch(InterruptedException e){}
	}
}
