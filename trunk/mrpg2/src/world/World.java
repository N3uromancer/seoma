package world;

import geom.Boundable;
import geomUtil.PartitionManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import world.modifier.Drawable;
import world.modifier.GameObject;
import world.modifier.Impassable;
import world.modifier.Movable;
import world.modifier.Updateable;
import world.unit.MovingUnit;

/**
 * represents a game world, manages all game objects added to the world
 * @author Jack
 *
 */
public class World
{
	/**
	 * stores game objects where it is necesary to keep a list of the objects
	 */
	HashMap<Class<?>, HashSet<GameObject>> objects = new HashMap<Class<?>, HashSet<GameObject>>();
	/**
	 * stores objects that must be spatially sorted for speed
	 */
	HashMap<Class<?>, PartitionManager> partitions = new HashMap<Class<?>, PartitionManager>();
	/**
	 * the width of the world
	 */
	double width = 2000;
	/**
	 * the height of the world
	 */
	double height = 2000;
	/**
	 * creates a new world loaded from the passed file
	 * @param f
	 */
	public World(File f)
	{
		this();
		/*try
		{
			FileInputStream fis = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(fis);
			
			try
			{
				String className = "";
				Class<?> c = getClass().getClassLoader().loadClass(className);
				Constructor<?> constructor = c.getC
			}
			catch(ClassNotFoundException a){}
		}
		catch(IOException e){}*/
	}
	/**
	 * creates a new blank world
	 */
	public World()
	{
		objects.put(Updateable.class, new HashSet<GameObject>());
		
		partitions.put(Drawable.class, new PartitionManager(0, 0, width, height, 30, 50, 20));
		partitions.put(Impassable.class, new PartitionManager(0, 0, width, height, 30, 50, 20));
		
		addGameObject(new MovingUnit(new double[]{50, 50}, 50, 50, 200, 200));
		addGameObject(new MovingUnit(new double[]{100, 100}, 100, 100, 400, 400));
		addGameObject(new MovingUnit(new double[]{500, 500}, 500, 500, 700, 700));
		addGameObject(new MovingUnit(new double[]{100, 100}, 50, 50, 200, 200));
	}
	/**
	 * writes the world to the passed data output stream
	 * @param dos
	 * @throws IOException
	 */
	public void writeWorld(DataOutputStream dos) throws IOException
	{
		dos.writeDouble(width);
		dos.writeDouble(height);
		
		HashSet<Object> go = new HashSet<Object>();
		for(Class<?> c: objects.keySet())
		{
			go.addAll(objects.get(c));
		}
		for(Class<?> c: partitions.keySet())
		{
			go.addAll(partitions.get(c).getElements());
		}
		for(Object o: go)
		{
			((GameObject)o).write(dos);
		}
	}
	/**
	 * adds a game object to the world, automatically sorts the object into
	 * its appropriate sets and partitions
	 * @param o
	 */
	public void addGameObject(GameObject o)
	{
		System.out.println("adding game object...");
		for(Class<?> c: objects.keySet())
		{
			if(c.isInstance(o))
			{
				System.out.println("added to "+c.getSimpleName());
				objects.get(c).add(o);
			}
		}
		addToPartitions(o);
		System.out.println("done");
	}
	/**
	 * adds the passed game object to the correct partition manager
	 * based off the class of the object
	 * @param o
	 */
	private void addToPartitions(GameObject o)
	{
		for(Class<?> c: partitions.keySet())
		{
			if(c.isInstance(o))
			{
				//System.out.println("sorted into "+c.getSimpleName()+" partition");
				partitions.get(c).add((Boundable)o);
			}
		}
	}
	/**
	 * removes the passed game object from partitions that it is contained within
	 * @param o
	 */
	private void removeFromPartitions(GameObject o)
	{
		for(Class<?> c: partitions.keySet())
		{
			if(c.isInstance(o))
			{
				partitions.get(c).remove((Boundable)o);
			}
		}
	}
	//dues ex machina
	public void updateWorld(double tdiff)
	{
		for(GameObject o: objects.get(Updateable.class))
		{
			if(o instanceof Movable)
			{
				removeFromPartitions(o);
			}
			((Updateable)o).update(tdiff);
			if(o instanceof Movable)
			{
				addToPartitions(o);
			}
		}
	}
	public void drawWorld(Graphics2D g, int width, int height)
	{
		g.setColor(Color.green);
		g.fillRect(0, 0, width, height);
		HashSet<Boundable> d = partitions.get(Drawable.class).intersects(0, 0, width, height);
		for(Boundable b: d)
		{
			((Drawable)b).draw(g, width, height);
		}
	}
}
