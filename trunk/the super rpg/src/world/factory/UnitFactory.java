package world.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import world.unit.Unit;

public class UnitFactory
{
	public Unit createUnit(byte type, short id)
	{
		/*
		 * temporary method filler here, this method should create the unit
		 * specified by the byte type loaded from the unit file
		 */
		Unit u = new Unit(true);
		u.setID(id);
		return u;
	}
	public UnitFactory(String unitStats)
	{
		File f = new File(unitStats);
		ArrayList<String> lines = new ArrayList<String>();
		try
		{
			Scanner scanner = new Scanner(f);
			while(scanner.hasNextLine())
			{
				lines.add(scanner.nextLine());
			}
		}
		catch(IOException e){}
		parseFile(lines);
	}
	public static void main(String[] args)
	{
		new UnitFactory("example unit stats.txt");
	}
	private void parseFile(ArrayList<String> lines)
	{
		//System.out.println("parsing unit stat file");
		int index = 0;
		byte type = Byte.MIN_VALUE;
		boolean skip = false;
		String strip = "";
		while(index < lines.size())
		{
			String line = lines.get(index);
			line = removeSpaces(line);
			for(int i = 0; i < line.length(); i++)
			{
				char c = line.charAt(i);
				if(c == '=')
				{
					skip = true;
				}
				else if(c == '-')
				{
					skip = false;
					parseStrip(strip);
					strip = "";
				}
				else if(!skip)
				{
					strip+=c;
				}
			}
			index++;
		}
		parseStrip(strip);
	}
	/**
	 * parses a strip of characters from the file
	 * @param strip
	 */
	private void parseStrip(String strip)
	{
		if(strip.length() > 0)
		{
			String[] s = strip.split(",");
			for(String temp: s)
			{
				System.out.println(temp);
				//interpret values here
			}
		}
	}
	private String removeSpaces(String line)
	{
		String temp = "";
		for(int i = 0; i < line.length(); i++)
		{
			char c = line.charAt(i);
			if(c != ' ')
			{
				temp+=c;
			}
		}
		return temp;
	}
}
