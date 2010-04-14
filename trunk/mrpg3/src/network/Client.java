package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

import world.World;
import display.Display;

public final class Client
{
	public Client(boolean host) throws IOException
	{
		MulticastSocket ms = new MulticastSocket(IOConstants.port);
		InetAddress group = InetAddress.getByName(IOConstants.address);
		ms.joinGroup(group);
		String ip = InetAddress.getLocalHost().getHostAddress();
		System.out.println("client ip = "+ip);
		World w = new World(host, ip);
		OperationExecutor oe = new OperationExecutor(w);
		new UDPReceiver(ms, oe);
		
		Display display = new Display(w);
		long sleepTime = 30;
		long start = System.currentTimeMillis();
		for(;;)
		{
			long tdiff = System.currentTimeMillis()-start;
			start = System.currentTimeMillis();
			w.updateWorld(tdiff/1000.);
			display.drawWorld();
			long updateDiff = System.currentTimeMillis()-start;
			if(sleepTime-updateDiff > 0)
			{
				try
				{
					Thread.sleep(sleepTime-updateDiff);
				}
				catch(InterruptedException e){}
			}
		}
	}
	public static void main(String[] args)
	{
		try
		{
			new Client(true);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
