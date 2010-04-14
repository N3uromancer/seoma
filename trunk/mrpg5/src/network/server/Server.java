package network.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JLabel;

import network.IOConstants;
import network.UDPReceiver;
import network.UDPWriteThread;
import network.operationExecutor.OperationExecutor;
import network.operationExecutor.serverOperation.ConnectClientOperation;
import world.RelevantSet;
import world.World;
import world.unit.Avatar;

/**
 * represents the game server, runs a "true" copy of the game
 * and updates clients by sending relevant information
 * @author Jack
 *
 */
public final class Server implements Runnable
{
	private DatagramSocket socket;
	
	private HashMap<String, RelevantSet> clients = new HashMap<String, RelevantSet>(); //key=client ip
	private Semaphore clientSem = new Semaphore(1, true); //controls access to the client map
	/**
	 * contains ip of clients that have completed the connection process, clients
	 * in this set are not sent the id of the avatar they control
	 */
	private HashSet<String> connectedClients = new HashSet<String>();
	
	World w;
	
	public Server()
	{
		try
		{
			socket = new DatagramSocket(IOConstants.serverPort);
			new Thread(this).start();
			
			JFrame f = new JFrame("Server");
			f.add(new JLabel("ip = "+InetAddress.getLocalHost()));
			f.pack();
			f.setLocationRelativeTo(null);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setVisible(true);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * called by the connect operation when a client is connecting to the server
	 * @param address the address of the client being connected
	 */
	public void connectClient(InetAddress address)
	{
		Avatar a = new Avatar(new double[]{50, 50});
		w.registerObject(a, (byte)0);
		RelevantSet rset = new RelevantSet(a.getID(), address);
		try
		{
			clientSem.acquire();
			clients.put(address.getHostAddress(), rset);
			clientSem.release();
		}
		catch(InterruptedException e){}
	}
	/**
	 * determines the avatar id of the client based off the
	 * passed ip address
	 * @return returns the id of the avatar associated with the passed ip
	 */
	public short getAvatarID(String ip)
	{
		return clients.get(ip).getObjectID();
	}
	public void run()
	{
		w = new World();
		OperationExecutor exe = new OperationExecutor();
		//exe.registerOperation(new UpdateWorldOperation(w));
		exe.registerOperation(new ConnectClientOperation(this));
		
		new UDPReceiver(socket, exe);
		
		long sleepTime = 30;
		long start = System.currentTimeMillis();
		long updates = 0;
		for(;;)
		{
			long tdiff = System.currentTimeMillis()-start;
			start = System.currentTimeMillis();
			w.updateWorld(tdiff/1000.);
			
			//test code for moving a unit and updating its position client side
			/*try
			{
				clientSem.acquire();
				
				for(String s: clients.keySet())
				{
					double m = 50;
					short id = clients.get(s).getObjectID();
					double[] l = ((Movable)w.getObject(id)).getLocation();
					((Movable)w.getObject(id)).setLocation(new double[]{l[0]+m*tdiff/1000., l[1]});
				}
				
				clientSem.release();
			}
			catch(InterruptedException e){}*/
			
			//System.out.println("world updated");
			
			try
			{
				clientSem.acquire();
				for(String s: clients.keySet())
				{
					clients.get(s).updateSet(w);
					
					byte[] b = clients.get(s).getRelevantData(70);
					InetAddress address = clients.get(s).getAddress();
					if(!connectedClients.contains(s))
					{
						//client hasnt completed connection, need to write the controller setup information
						try
						{
							ByteArrayOutputStream baos = new ByteArrayOutputStream(b.length+3);
							baos.write(b);
							baos.write(IOConstants.controllerSetup);
							DataOutputStream dos = new DataOutputStream(baos);
							dos.writeShort(clients.get(s).getObjectID());
							b = baos.toByteArray();
						}
						catch(IOException e){}
					}
					new UDPWriteThread(b, socket, address, true);
				}
				clientSem.release();
			}
			catch(InterruptedException e){};
			
			long updateDiff = System.currentTimeMillis()-start;
			if(sleepTime-updateDiff > 0)
			{
				try
				{
					Thread.sleep(sleepTime-updateDiff);
				}
				catch(InterruptedException e){}
			}
			
			updates++;
			if(updates % 1000 == 0)
			{
				exe.printExecutionTimes();
				System.out.println("---------------------------------");
			}
		}
	}
	public static void main(String[] args)
	{
		new Server();
	}
}