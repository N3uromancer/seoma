package world.initializer;

import java.util.HashMap;

/**
 * loads and registers all initializables
 * @author Jack
 *
 */
public final class Initializer
{
	private HashMap<Byte, String> ini = new HashMap<Byte, String>();
	private HashMap<String, Byte> iniIDmap = new HashMap<String, Byte>();
	
	/**
	 * initializables to be loaded with the initializer
	 * @param i the class names of the initializables to be register with the initializer
	 */
	public Initializer(String[] initializables)
	{
		for(byte i = 0, index = Byte.MIN_VALUE; i < initializables.length; i++, index++)
		{
			ini.put(index, initializables[i]);
			iniIDmap.put(initializables[i], index);
		}
	}
	/**
	 * gets the initializable id code for a specific initializable
	 * @param i
	 * @return returns the id of the initializable
	 */
	public byte getInitializeID(Initializable i)
	{
		return iniIDmap.get(i.getClass().getName());
	}
	/**
	 * loads a new initializable from the passed byte id
	 * @param id
	 * @return returns the newly loaded initializable
	 */
	public Initializable getInitializable(byte id)
	{
		if(!ini.containsKey(id))
		{
			System.err.println("illegal initialization load, id="+id+" does not exist");
			System.exit(0);
		}
		Object o = null;
		try
		{
			ClassLoader loader = getClass().getClassLoader();
			Class<?> c = loader.loadClass(ini.get(id));
			//Constructor<?> constr = c.getConstructor();
			//constr.newInstance();
			o = c.newInstance();
		}
		catch(Exception e)
		{
			System.err.println("illegal initialization load, "+ini.get(id)+" does not exist");
			System.exit(0);
		}
		
		return (Initializable)o;
	}
}
