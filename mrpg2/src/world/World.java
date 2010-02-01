package world;

import java.util.HashMap;
import java.util.HashSet;
import modifier.*;

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
	}
	public void addGameObject(GameObject g)
	{
		for(Class<?> c: g.getClass().getInterfaces())
		{
			if(objects.containsKey(c))
			{
				objects.get(c).add(g);
			}
		}
	}
	public void updateWorld(double tdiff)
	{
		
	}
}
