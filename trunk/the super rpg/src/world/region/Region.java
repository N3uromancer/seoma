package world.region;

import geom.Boundable;
import geom.Rectangle;
import geomUtil.PartitionManager;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import world.World;
import world.modifier.Drawable;
import world.modifier.Moveable;
import world.networkUpdateable.NetworkUpdateable;
import world.terrain.Terrain;
import world.unit.Unit;
import display.Camera;

/**
 * defines a section of the game world, each region is specified in the world by
 * its byte id, each game object belongs to the world
 * @author Jack
 *
 */
public class Region
{
	LinkedList<NetworkUpdateable> u = new LinkedList<NetworkUpdateable>();
	HashMap<Short, NetworkUpdateable> uidMap = new HashMap<Short, NetworkUpdateable>(); //updateable id map
	HashSet<NetworkUpdateable> drawRemove = new HashSet<NetworkUpdateable>(); //contains ids of objects that are dead and need to be removed from the draw partition
	Semaphore uSem = new Semaphore(1, true); //controls access to both the linked list and the id map
	
	PartitionManager drawables;
	Semaphore dSem = new Semaphore(1, true);
	
	private Terrain[][] t;
	private int[] dim; //the dimensions of the region
	
	public Region()
	{
		try
		{
			loadRegion(new File("test map"));
		}
		catch(Exception e){}
		//setSize(1000, 1000);
		drawables = new PartitionManager(0, 0, dim[0], dim[1], 20, 100, 400);
	}
	/**
	 * checks to see if the passed rectangle intersects the terrain of the region
	 * @param r
	 * @param allowed a set of terrain that the passed rectangle is allowed to intersect
	 * @return returns true if the passed rectangle intersects non-allowed terrain,
	 * false otherwise
	 */
	private boolean intersectsTerrain(Rectangle r, HashSet<Terrain> allowed)
	{
		for(int x = (int)(r.getLocation()[0]/World.gridSize); x <= (int)((r.getLocation()[0]+r.getWidth())/World.gridSize); x++)
		{
			for(int y = (int)(r.getLocation()[1]/World.gridSize); y <= (int)((r.getLocation()[1]+r.getHeight())/World.gridSize); y++)
			{
				//System.out.println(t[x][y]+", allowed="+allowed.contains(t[x][y]));
				if(!allowed.contains(t[x][y]))
				{
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * gets the network objects associated with this region, this method should
	 * always be used in conjunction with the get region semaphore method
	 * to ensure no concurrency problems
	 * @return returns a list of network updateable object registered with this region
	 */
	public LinkedList<NetworkUpdateable> getNetworkObjects()
	{
		return u;
	}
	public Semaphore getSemaphore()
	{
		return uSem;
	}
	public byte getRegionID()
	{
		return Byte.MIN_VALUE;
	}
	/**
	 * gets the units intersected by the passed boundable object
	 * @param b
	 * @return returns the units intersected by the passed boundable object
	 */
	public HashSet<Unit> getIntersectedUnits(Boundable b)
	{
		HashSet<Unit> units = new HashSet<Unit>();
		try
		{
			dSem.acquire();
			HashSet<Boundable> intersections = drawables.intersects(b);
			dSem.release();
			for(Boundable temp: intersections)
			{
				if(temp instanceof Unit)
				{
					units.add((Unit)temp);
				}
			}
			units.remove(b);
		}
		catch(InterruptedException e){}
		return units;
	}
	/**
	 * registers an object with the region
	 * @param o
	 */
	public void registerObject(NetworkUpdateable o)
	{
		try
		{
			uSem.acquire();
			u.add(o);
			uidMap.put(o.getID(), o);
			uSem.release();
			if(o instanceof Drawable)
			{
				dSem.acquire();
				drawables.add((Drawable)o);
				dSem.release();
			}
		}
		catch(InterruptedException e){}
	}
	/**
	 * updates an object contained within this region
	 * @param id the id of the object to be updated
	 * @param buff the information to be loaded by the object
	 */
	public void updateObject(short id, byte[] buff)
	{
		try
		{
			uSem.acquire();
			uidMap.get(id).loadState(buff);
			uSem.release();
		}
		catch(InterruptedException e){}
		catch(NullPointerException e)
		{
			//for debug purposes
			System.out.println(id+" cannot be updated, buff length = "+buff.length);
			e.printStackTrace();
		}
	}
	/**
	 * gets a single network object from the region, this method should be used in conjunction
	 * to the get region semaphore method in order to assure no concurrency problems
	 * @param id the id of the network object to retrieve
	 * @return
	 */
	public NetworkUpdateable getNetworkObject(short id)
	{
		return uidMap.get(id);
	}
	/**
	 * gets the state for a single object contained within the region
	 * @param id
	 * @return returns the state of the object with the passed id
	 */
	public byte[] getState(short id)
	{
		byte[] state = null;
		try
		{
			uSem.acquire();
			state = uidMap.get(id).getState();
			uSem.release();
		}
		catch(InterruptedException e){}
		return state;
	}
	/**
	 * updates the objects contained within the region
	 * @param w
	 * @param tdiff
	 */
	public void updateRegion(World w, double tdiff)
	{
		HashSet<NetworkUpdateable> destroy = new HashSet<NetworkUpdateable>(); //objects to be destroyed
		try
		{
			uSem.acquire();
			Iterator<NetworkUpdateable> i = u.iterator();
			while(i.hasNext())
			{
				NetworkUpdateable u = i.next();
				if(!u.isDead())
				{
					if(!u.isGhost())
					{
						double[] pastLoc = null;
						if(u instanceof Moveable)
						{
							pastLoc = ((Moveable)u).getLocation();
						}
						u.update(w, tdiff);
						if(u instanceof Moveable)
						{
							Rectangle bounds = ((Moveable)u).getBounds();
							double[] l = ((Moveable)u).getLocation();
							if(intersectsTerrain(bounds, ((Moveable)u).getMovementType()) ||
									l[0] < 0 || l[0] > dim[0] || l[1] < 0 || l[1] > dim[1])
							{
								((Moveable)u).setLocation(pastLoc);
							}
						}
					}
					else
					{
						u.simulate(w, tdiff);
					}
				}
				else
				{
					destroy.add(u);
					i.remove();
				}
			}
			uSem.release();
		}
		catch(InterruptedException e){}
		if(destroy.size() > 0)
		{
			//destroyed objects removed after update to avoid deadlocking when calling the world destroy method
			for(NetworkUpdateable u: destroy)
			{
				w.destroyObject(u.getID());
				if(u instanceof Drawable)
				{
					drawRemove.add(u);
				}
				uidMap.remove(u.getID());
			}
		}
	}
	/**
	 * sets the size of the region
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height)
	{
		dim = new int[2];
		dim[0] = width/World.gridSize*World.gridSize;
		dim[1] = height/World.gridSize*World.gridSize;
		
		Terrain[][] temp = new Terrain[width/World.gridSize][height/World.gridSize];
		for(int x = 0; x < temp.length; x++)
		{
			for(int y = 0; y < temp[0].length; y++)
			{
				temp[x][y] = Terrain.grass;
			}
		}
		if(t != null)
		{
			//transfers over already placed terrain
			for(int x = 0; x < t.length; x++)
			{
				for(int y = 0; y < t[0].length; y++)
				{
					try
					{
						temp[x][y] = t[x][y];
					}
					catch(ArrayIndexOutOfBoundsException e){}
				}
			}
		}
		t = temp;
	}
	/**
	 * loads the region from the specified file
	 * @param f
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadRegion(File f) throws IOException, ClassNotFoundException
	{
		System.out.print("loading region... ");
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		int width = dis.readInt();
		int height = dis.readInt();
		dim = new int[]{width*World.gridSize, height*World.gridSize};
		t = new Terrain[width][height];
		
		for(int x = 0; x < t.length; x++)
		{
			for(int y = 0; y < t[0].length; y++)
			{
				String name = dis.readUTF();
				t[x][y] = Terrain.valueOf(name);
			}
		}
		System.out.println("done");
	}
	public void drawRegion(Graphics2D g, DisplayMode dm, Camera c)
	{
		//g.setColor(Color.black);
		//g.drawRect(0, 0, dim[0], dim[1]);
		//g.fillRect(0, 0, 50, 50);

		double[] l = c.getLocation();
		l[1]*=-1;
		Rectangle r = c.getViewBounds();
		for(int x = (int)(l[0]/World.gridSize) < 0? 0: (int)(l[0]/World.gridSize); x < t.length && x <= (int)((l[0]+r.getWidth())/World.gridSize); x++)
		{
			for(int y = (int)(l[1]/World.gridSize) < 0? 0: (int)(l[1]/World.gridSize); y < t[0].length && y <= (int)((l[1]+r.getHeight())/World.gridSize)+1; y++)
			{
				g.setColor(t[x][y].getColor());
				g.fillRect(x*World.gridSize, y*World.gridSize, World.gridSize, World.gridSize);
			}
		}
		g.setColor(Color.black);
		for(int i = 0; i <= dim[0]; i+=World.gridSize)
		{
			g.drawLine(i, 0, i, dim[1]);
		}
		for(int i = 0; i <= dim[1]; i+=World.gridSize)
		{
			g.drawLine(0, i, dim[0], i);
		}
		
		try
		{
			HashSet<NetworkUpdateable> remove = null;
			if(drawRemove.size() > 0)
			{
				//checking the size in the line above might not be thread safe
				remove = new HashSet<NetworkUpdateable>();
				uSem.acquire();
				remove.addAll(drawRemove);
				drawRemove = new HashSet<NetworkUpdateable>();
				uSem.release();
			}
			
			dSem.acquire();
			if(remove != null)
			{
				for(NetworkUpdateable n: remove)
				{
					drawables.remove((Boundable)n);
				}
			}
			HashSet<Boundable> b = drawables.intersects(c.getViewBounds());
			dSem.release();
			for(Boundable temp: b)
			{
				if(((Drawable)temp).isDisplayed())
				{
					((Drawable)temp).draw(g);
				}
			}
		}
		catch(InterruptedException e){}
	}
}
