package script;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class ScriptParserV1 implements ScriptParser
{
	HashSet<Character> skip = new HashSet<Character>(); //characters that denote lines to be skipped
	HashMap<String, Byte> nameMap = new HashMap<String, Byte>(); //maps the names of variables to their byte id
	
	HashMap<Byte, Boolean> booleans = new HashMap<Byte, Boolean>();
	HashMap<Byte, Short> shorts = new HashMap<Byte, Short>();
	HashMap<Byte, Double> doubles = new HashMap<Byte, Double>();
	
	byte newID = Byte.MIN_VALUE; //used to assign ids to variables
	
	public ScriptParserV1()
	{
		skip.add('-');
		skip.add('=');
		skip.add('>');
		skip.add('<');
	}
	public HashMap<Byte, Boolean> getBooleans()
	{
		return booleans;
	}
	public HashMap<Byte, Double> getDoubles()
	{
		return doubles;
	}
	public HashMap<Byte, Short> getShorts()
	{
		return shorts;
	}
	public HashMap<String, Byte> getVariables()
	{
		return nameMap;
	}
	public double getVersion()
	{
		return 1;
	}
	public void parseScript(File script) throws IOException
	{
		Scanner s = new Scanner(script);
		s.nextLine(); //first line version
		
		String line;
		while(s.hasNextLine())
		{
			line = s.nextLine();
			if(!skip.contains(line.charAt(0)))
			{
				String temp = line.trim();
				String[] values = temp.split("=");
				String[] typeName = values[0].split(" ");
				sortVariable(typeName[0].trim(), typeName[1].trim(), values[2].trim());
			}
		}
	}
	private void sortVariable(String type, String name, String value)
	{
		nameMap.put(name, newID);
		if(type.equals("boolean"))
		{
			booleans.put(newID, Boolean.parseBoolean(value));
		}
		else if(type.equals("short"))
		{
			shorts.put(newID, Short.parseShort(value));
		}
		else if(type.equals("double"))
		{
			doubles.put(newID, Double.parseDouble(value));
		}
		newID++;
	}
}
