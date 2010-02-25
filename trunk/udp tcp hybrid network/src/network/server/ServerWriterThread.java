package network.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import network.Operation;

/**
 * responsible for writing data to the clients
 * @author Jack
 *
 */
public final class ServerWriterThread implements Runnable
{
	private LinkedBlockingQueue<byte[]> q = new LinkedBlockingQueue<byte[]>();
	private Semaphore s = new Semaphore(1, true);
	private OutputStream os;
	boolean disconnecting = false;
	
	public ServerWriterThread(Socket s)
	{
		try
		{
			os = s.getOutputStream();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void run()
	{
		try
		{
			while(!disconnecting)
			{
				while(q.size() > 0 && !disconnecting)
				{
					try
					{
						s.acquire();
						byte[] buf = q.poll();
						s.release();
						os.write(buf);
					}
					catch(InterruptedException e){}
				}
				/*
				 * to protect against waiting if disconnecting is set to true
				 * while the thread is in the above while loop
				 */
				if(!disconnecting)
				{
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
			os.write(Operation.serverDisconnect);
			//System.out.println("server disconnect message written to client");
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		//System.out.println("terminating server writer thread");
		//System.out.println("--------------------------------------------------");
	}
	/**
	 * adds the passed byte buffer to the queue to be written
	 * the output stream, notifies the write thread to start if
	 * it is waiting for data
	 * @param buff
	 */
	public synchronized void add(byte[] buff)
	{
		try
		{
			s.acquire();
			q.add(buff);
			s.release();
			notify();
		}
		catch(InterruptedException e){}
	}
	/**
	 * disconnects this writer thread
	 */
	public synchronized void disconnect()
	{
		disconnecting = true;
		notify();
	}
}
