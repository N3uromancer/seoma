package world;

import geom.Boundable;
import geomUtil.PartitionManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import pathfinder.Pathfinder;
import pathfinder.localPlanner.LocalPlannerV1;
import pathfinder.nodeGenerator.RandomNodeGenerator;
import world.modifier.Drawable;
import world.modifier.PathObstacle;
import world.modifier.Temporary;
import world.modifier.Updateable;
import world.terrain.CircleTerrain;
import display.Camera;


public class World
{
	HashMap<Class<?>, PartitionManager> partitions = new HashMap<Class<?>, PartitionManager>();
	HashMap<Class<?>, HashSet<Object>> lists = new HashMap<Class<?>, HashSet<Object>>();
	HashMap<Class<?>, Semaphore> listSem = new HashMap<Class<?>, Semaphore>();
	
	//maps objects to the lists that contain them
	HashMap<Object, HashSet<Class<?>>> objListMap = new HashMap<Object, HashSet<Class<?>>>();
	//maps objects to partitions that contain them
	HashMap<Object, HashSet<Class<?>>> objPartMap = new HashMap<Object, HashSet<Class<?>>>();
	
	Pathfinder pf;
	
	int width = 1000; //world width
	int height = 1000; //world height
	
	long drawTime = 0;
	long draws = 0;
	
	/**
	 * generates a default world with random terrain
	 */
	public World()
	{
		createWorld();
		
		HashSet<PathObstacle> obstacles = new HashSet<PathObstacle>();
		double minR = 30;
		double maxR = 80;
		for(int i = 0; i < 0; i++)
		{
			double x = Math.random()*width;
			double y = Math.random()*height;
			double r = Math.random()*(maxR-minR);
			PathObstacle temp = new CircleTerrain(new double[]{x, y}, minR+r);
			registerObject(temp);
			obstacles.add(temp);
		}
		pf = new Pathfinder(obstacles, new double[]{7}, width, height, new RandomNodeGenerator(), new LocalPlannerV1());
	}
	/**
	 * gets a reference to the pathfinder used by this world
	 * @return returns the pathfinder for this world
	 */
	public Pathfinder getPathfinder()
	{
		return pf;
	}
	/**
	 * sets up the world partitions
	 */
	private void createWorld()
	{
		partitions.put(Drawable.class, new PartitionManager(0, 0, width, height, 50, 100, 100));
		partitions.put(PathObstacle.class, new PartitionManager(0, 0, width, height, 50, 100, 100));
		
		lists.put(Updateable.class, new HashSet<Object>());
		for(Class<?> c: lists.keySet())
		{
			listSem.put(c, new Semaphore(1, true));
		}
	}
	public PartitionManager getPartition(Class<?> c)
	{
		return partitions.get(c);
	}
	/**
	 * registers an object with the world based off the interfaces defining
	 * how the object is to interact with the world
	 * 
	 * each object is automaticaly placed within the correct lists and partitions
	 * upon its registering with the world
	 * 
	 * a set of the partitions and lists in which the object is stored is also created
	 * and updated
	 * @param o
	 */
	public void registerObject(Object o)
	{
		//System.out.println("adding "+o.getClass().getSimpleName()+"...");
		if(o instanceof Boundable)
		{
			for(Class<?> c: partitions.keySet())
			{
				if(c.isInstance(o))
				{
					//System.out.println("added to partitions, "+c.getSimpleName());
					partitions.get(c).add((Boundable)o);
					if(!objPartMap.containsKey(o))
					{
						objPartMap.put(o, new HashSet<Class<?>>());
					}
					objPartMap.get(o).add(c);
				}
			}
		}
		for(Class<?> c: lists.keySet())
		{
			if(c.isInstance(o))
			{
				try
				{
					listSem.get(c).acquire();
					
					//System.out.println("added to list, "+c.getSimpleName());
					lists.get(c).add(o);
					if(!objListMap.containsKey(o))
					{
						objListMap.put(o, new HashSet<Class<?>>());
					}
					objListMap.get(o).add(c);
					
					listSem.get(c).release();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	public void drawWorld(Graphics2D g, Camera c, int width, int height)
	{
		g.setColor(Color.green);
		g.fillRect(0, 0, width, height);
		g.setTransform(c.getTransform());
		g.setColor(Color.black);
		g.drawRect(0, 0, this.width, this.height);
		//te.drawTerrain(g, width, height);
		
		//p.drawPathGraph(g, 7);

		g.setColor(Color.red);
		long start = System.currentTimeMillis();
		for(Boundable b: partitions.get(Drawable.class).intersects(c.getViewBounds()))
		{
			((Drawable)b).draw(g);
		}
		pf.drawPathGraph(g);
		
		drawTime+=System.currentTimeMillis()-start;
		draws++;
		//System.out.println(draws*1000./drawTime+"\t = draws/sec, "+draws+" [draws] / "+drawTime/1000.+" [time]");
	}
	/**
	 * updates the world by the passed time difference
	 * @param tdiff
	 */
	public void updateWorld(double tdiff)
	{
		HashSet<Object> deadObj = new HashSet<Object>();
		try
		{
			listSem.get(Updateable.class).acquire();
			for(Object o: lists.get(Updateable.class))
			{
				Boundable b = null;
				if(o instanceof PathObstacle)
				{
					b = (Boundable)o;
					partitions.get(PathObstacle.class).remove(b);
				}
				((Updateable)o).update(this, tdiff);
				if(o instanceof Temporary)
				{
					if(((Temporary)o).isDead())
					{
						deadObj.add(o);
					}
				}
				if(b != null)
				{
					partitions.get(PathObstacle.class).add(b);
				}
			}
			listSem.get(Updateable.class).release();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		//must be done after update loop to avoid concurrent modification exceptions
		for(Object o: deadObj)
		{
			removeObject(o);
		}
	}
	/**
	 * removes the passed object from the world
	 * @param o
	 */
	private void removeObject(Object o)
	{
		//System.out.println("removing "+o.getClass().getSimpleName()+"...");
		if(objPartMap.containsKey(o))
		{
			for(Class<?> c: objPartMap.get(o))
			{
				partitions.get(c).remove((Boundable)o);
				//System.out.println("removed from "+c.getSimpleName()+" partition");
			}
		}
		objPartMap.remove(o);
		if(objListMap.containsKey(o))
		{
			for(Class<?> c: objListMap.get(o))
			{
				//lists.get(c).remove(o);
				try
				{
					listSem.get(c).acquire();
					lists.get(c).remove(o);
					//System.out.println("removed from "+c.getSimpleName()+" list");
					listSem.get(c).release();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		objListMap.remove(o);
	}
}
