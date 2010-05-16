package network.writeController;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * reliably writes queued byte buffers to the server or client via tcp
 * @author Jack
 *
 */
public final class TCPWriteThread extends Thread
{
	private LinkedBlockingQueue<byte[]> q = new LinkedBlockingQueue<byte[]>();
	private Socket s;
	
	public TCPWriteThread(Socket s)
	{
		this.s = s;
		
		/*Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();*/
		
		setDaemon(true);
		start();
	}
	public void run()
	{
		for(;;)
		{
			while(q.size() > 0)
			{
				byte[] b = q.poll();
				try
				{
					s.getOutputStream().write(b);
				}
				catch(IOException e)
				{
					System.err.println("tcp write thread io exception, cannot write to socket stream");
					e.printStackTrace();
				}
			}
			synchronized(this)
			{
				try
				{
					wait();
				}
				catch(InterruptedException e)
				{
					System.out.println("tcp write thread interrupted exception caught, breaking loop");
					break;
				}
			}
		}
	}
	/**
	 * adds the passed byte buffer to the queue to be written to the socket
	 * @param b
	 */
	public synchronized void queueData(byte[] b)
	{
		q.add(b);
		notify();
	}
}
