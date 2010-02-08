package world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;

import modifier.Drawable;
import modifier.GameObject;
import modifier.Movable;
import modifier.Updateable;

/**
 * manages all the zones that make up the game world
 * @author Jack
 *
 */
public class World
{
	HashMap<Class<?>, HashSet<GameObject>> objects = new HashMap<Class<?>, HashSet<GameObject>>();
	
	public World()
	{
		objects.put(Updateable.class, new HashSet<GameObject>());
		objects.put(Movable.class, new HashSet<GameObject>());
		objects.put(Drawable.class, new HashSet<GameObject>());
	}
	public void addGameObject(GameObject g)
	{
		System.out.println("adding game object...");
		for(Class<?> c: objects.keySet())
		{
			if(c.isInstance(g))
			{
				System.out.println("added to "+c.getSimpleName());
				objects.get(c).add(g);
			}
		}
		System.out.println("done");
	}
	//dues ex machina
	public void updateWorld(double tdiff)
	{
		for(GameObject o: objects.get(Updateable.class))
		{
			((Updateable)o).update(tdiff);
		}
	}
	public void drawWorld(Graphics2D g, int width, int height)
	{
		g.setColor(Color.green);
		g.fillRect(0, 0, width, height);
		for(GameObject o: objects.get(Drawable.class))
		{
			((Drawable)o).draw(g, width, height);
		}
	}
}
