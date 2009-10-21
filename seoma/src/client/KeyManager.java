package client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import box.Box;

/**
 * manages key actions for the game
 * @author Jack
 *
 */
public class KeyManager
{
	//represents the keys that are down
	HashMap<Byte, LinkedList<Character>> down = new HashMap<Byte, LinkedList<Character>>();
	HashMap<Character, double[]> m = new HashMap<Character, double[]>(); //the actions of the keys
	
	public static final int considerAllInputs = -1;
	
	public KeyManager()
	{
		double movement = 20;
		m.put('w', new double[]{0, -movement});
		m.put('d', new double[]{movement, 0});
		m.put('s', new double[]{0, movement});
		m.put('a', new double[]{-movement, 0});
	}
	public void registerNewPlayer(byte owner)
	{
		System.out.println("new player register, owner = "+owner);
		down.put(owner, new LinkedList<Character>());
	}
	public void registerKey(byte owner, char c, boolean pressed)
	{
		LinkedList<Character> ll = down.get(owner);
		ll.add(c);
		down.put(owner, ll);
	}
	/**
	 * updates the game units
	 * @param b the list of game units
	 * @param inputsToConsider the number of already received inputs to considered
	 * when updating the units
	 */
	public void updateUnits(Box[] b, int inputsToConsider)
	{
		for(byte i = 0; i < b.length; i++)
		{
			if(b[i] != null)
			{
				try
				{
					LinkedList<Character> ll = down.get(i);
					Iterator<Character> ci = ll.iterator();
					int considered = 0;
					while(ci.hasNext() && (considered <= inputsToConsider || inputsToConsider == -1))
					{
						b[i].translate(m.get(ci.next()));
						ci.remove();
						considered++;
					}
				}
				catch(Exception e)
				{
					System.out.println(i);
					e.printStackTrace();
				}
			}
		}
	}
}
