package network.operationExecutor;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import network.Connection;

/**
 * executes operations of received packets to modify the world
 * and/or client game state
 * 
 * records the average time in ms that it takes for operations to complete
 * @author Jack
 *
 */
public final class OperationExecutor implements Runnable
{
	private LinkedBlockingQueue<byte[]> q = new LinkedBlockingQueue<byte[]>();
	private Map<byte[], Connection> connections = Collections.synchronizedMap(new HashMap<byte[], Connection>()); //maps each byte buffer with the connection from which it came
	
	private HashMap<Byte, Operation> operations = new HashMap<Byte, Operation>();
	private HashMap<Operation, Long> exeTime = new HashMap<Operation, Long>(); //total execution time
	private HashMap<Operation, Long> exeTotal = new HashMap<Operation, Long>(); //total number of executions
	private Semaphore opSem = new Semaphore(1, true); //semaphore for accessing the operation map and avg time map
	
	/**
	 * creates a new operation executor with no registered operation
	 */
	public OperationExecutor()
	{
		new Thread(this).start();
	}
	/**
	 * registers an operation with this executor to be carried out,
	 * if an operation is already registered with the same id as the
	 * operation passed, the passed operation overwrites it
	 * @param o the operation to be registered, the id of the operation
	 * cannot be 0 because an operation id of zero denotes the end
	 * of a packet
	 */
	public void registerOperation(Operation o)
	{
		if(o.getID() == 0)
		{
			System.err.println("operation "+o.getClass().getSimpleName()+" id = 0");
			System.exit(0);
		}
		try
		{
			opSem.acquire();
			operations.put(o.getID(), o);
			
			exeTime.put(o, (long)0);
			exeTotal.put(o, (long)0);
			opSem.release();
		}
		catch(InterruptedException e){}
	}
	public void run()
	{
		for(;;)
		{
			while(q.size() > 0)
			{
				try
				{
					byte[] data = q.poll();
					ByteBuffer buff = ByteBuffer.wrap(data);
					boolean done = false;
					while(buff.hasRemaining() && !done)
					{
						byte operation = buff.get();
						//System.out.println("operation = "+operation);
						if(operation != 0)
						{
							opSem.acquire();
							if(operations.containsKey(operation))
							{
								Operation o = operations.get(operation);
								long start = System.currentTimeMillis();
								o.performOperation(buff, connections.get(data));
								exeTime.put(o, exeTime.get(o)+System.currentTimeMillis()-start);
								exeTotal.put(o, exeTotal.get(o)+1);
								connections.remove(buff);
							}
							else
							{
								System.err.println("unrecognized operation, "+operation);
								System.exit(0);
							}
							opSem.release();
						}
						else
						{
							done = true;
						}
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
	 * prints the average execution times for operations performed
	 * by the executor
	 */
	public void printExecutionTimes()
	{
		for(Operation o: exeTime.keySet())
		{
			//System.out.print(exeTime.get(o)*1./exeTotal.get(o)+"\t"+o.getClass().getSimpleName()+"\n");
			System.out.print(o.getClass().getSimpleName()+"\t"+exeTime.get(o)/1000./exeTotal.get(o)+"\n");
		}
	}
	/**
	 * adds the passed byte buffer to the queue to be executed
	 * and notifies the execution thread to start
	 * @param packet
	 */
	public synchronized void add(byte[] buff, Connection c)
	{
		q.add(buff);
		connections.put(buff, c);
		notify();
	}
}
