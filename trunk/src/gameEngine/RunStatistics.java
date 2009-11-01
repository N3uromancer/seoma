package gameEngine;

import java.util.HashMap;

/**
 * stores the run statistics for various functions
 * @author Jack
 *
 */
public class RunStatistics
{
	/**
	 * stores the statistics of various functions for various classes
	 * 
	 * key1 = name of the class
	 * key2 = name of the function
	 * value = time for the function
	 */
	public static HashMap<String, HashMap<String, Long>> t = new HashMap<String, HashMap<String, Long>>();
	
	/**
	 * adds a time amount to the total time for a function
	 * @param className the name of the class where the funtion is
	 * @param functionName the name of the function
	 * @param time the time the function took to complete, automatically added
	 * to total time for the function
	 */
	public static void putTime(String className, String functionName, long time)
	{
		if(t.get(className) != null)
		{
			if(t.get(className).get(functionName) != null)
			{
				HashMap<String, Long> values = t.get(className);
				long l = values.get(functionName)+time;
				values.put(functionName, l);
				t.put(className, values);
			}
			else
			{
				HashMap<String, Long> values = t.get(className);
				values.put(functionName, time);
				t.put(className, values);
			}
		}
		else
		{
			HashMap<String, Long> temp = new HashMap<String, Long>();
			temp.put(functionName, time);
			t.put(className, temp);
		}
	}
}
