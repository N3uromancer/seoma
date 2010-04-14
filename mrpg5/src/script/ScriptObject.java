package script;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ScriptObject
{
	ScriptParser parser;
	
	public ScriptObject(File f)
	{
		try
		{
			Scanner s = new Scanner(f);
			String line = s.nextLine();
			double version = Double.parseDouble(line.split(" ")[1]);
			
			ScriptParser[] sp = new ScriptParser[1];
			sp[0] = new ScriptParserV1();
			
			for(int i = 0; i < sp.l)
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
}
