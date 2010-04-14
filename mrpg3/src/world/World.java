package world;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import world.unit.Avatar;

/**
 * encompasses the entirety of the game world
 * @author Jack
 *
 */
public class World implements KeyListener
{
	HashMap<Byte, Region> regions = new HashMap<Byte, Region>();
	HashMap<String, Avatar> avatars = new HashMap<String, Avatar>();
	HashSet<Byte> control = new HashSet<Byte>(); //regions controlled by this world client
	
	HashSet<Character> down = new HashSet<Character>(); //keys that are currently depressed by the user
	Semaphore downSem = new Semaphore(1, true);
	HashMap<Character, double[]> actions = new HashMap<Character, double[]>();
	String ip;
	
	/**
	 * creates a new world
	 * @param host boolean for determining if this is the host world,
	 * the host world starts with control of all regions
	 * @param ip the ip address of the client, used as the key to
	 * reference the players avatar
	 */
	public World(boolean host, String ip)
	{
		System.out.println("starting world...");
		this.ip = ip;
		Region e = new Region();
		regions.put(e.getID(), e);
		
		Avatar a = new Avatar(new double[]{50, 50});
		a.setRegion(e.getID());
		avatars.put(ip, a);
		

		double m = 200;
		actions.put('w', new double[]{0, -m});
		actions.put('d', new double[]{m, 0});
		actions.put('s', new double[]{0, m});
		actions.put('a', new double[]{-m, 0});
		
		if(host)
		{
			System.out.print("loading host world settings... ");
			for(Byte b: regions.keySet())
			{
				control.add(b);
			}
			System.out.println("done");
		}
	}
	/**
	 * gets the environment referenced by the passed id
	 * @param id
	 * @return
	 */
	public Region getRegion(byte id)
	{
		return regions.get(id);
	}
	/**
	 * gets the avatar for the passed ip
	 * @param ip
	 * @return
	 */
	public Avatar getAvatar(String ip)
	{
		return avatars.get(ip);
	}
	public void drawWorld(Graphics2D g, DisplayMode dm)
	{
		//long start = System.currentTimeMillis();
		Avatar a = avatars.get(ip);
		regions.get(a.getRegion()).drawRegion(g, dm);
		a.draw(g, dm);
		//System.out.println(System.currentTimeMillis()-start);
	}
	public void updateWorld(double tdiff)
	{
		try
		{
			downSem.acquire();
			for(Character c: down)
			{
				if(actions.containsKey(c))
				{
					double[] l = avatars.get(ip).getLocation();
					l[0]+=actions.get(c)[0]*tdiff;
					l[1]+=actions.get(c)[1]*tdiff;
					avatars.get(ip).setLocation(l);
				}
			}
			downSem.release();
		}
		catch(InterruptedException e){}
	}
	public void keyPressed(KeyEvent e)
	{
		if(!down.contains(e.getKeyChar()))
		{
			try
			{
				downSem.acquire();
				down.add(e.getKeyChar());
				downSem.release();
			}
			catch(InterruptedException a){}
		}
	}
	public void keyReleased(KeyEvent e)
	{
		try
		{
			downSem.acquire();
			down.remove(e.getKeyChar());
			downSem.release();
		}
		catch(InterruptedException a){}
	}
	public void keyTyped(KeyEvent e)
	{
		if(e.getKeyChar() == KeyEvent.VK_ESCAPE)
		{
			System.exit(0);
		}
	}
}