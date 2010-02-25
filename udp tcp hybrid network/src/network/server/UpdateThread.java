package network.server;

import java.net.Socket;


/**
 * the thread that sends out the update messages to the clients
 * @author Jack
 *
 */
public final class UpdateThread implements Runnable
{
	private ServerAcceptorThread sat;
	
	public UpdateThread(ServerAcceptorThread sat)
	{
		this.sat = sat;
	}
	public void run()
	{
		long sleep = 20;
		for(;;)
		{
			try
			{
				sat.getSocketSemaphore().acquire();
				for(Socket s:sat.getSockets())
				{
					//s.get
				}
				sat.getSocketSemaphore().release();
				
				Thread.sleep(20);
			}
			catch(InterruptedException e){}
		}
	}
}
