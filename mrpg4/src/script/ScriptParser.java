package script;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * an interface for a script parser, each parser is meant to parse
 * one script and store the information obtained
 * @author Jack
 *
 */
public interface ScriptParser
{
	public double getVersion();
	/**
	 * gets a map containing the names of all the variables for the script
	 * object and their byte ids
	 * @return gets all the variables for the script
	 */
	public HashMap<String, Byte> getVariables();
	public HashMap<Byte, Boolean> getBooleans();
	public HashMap<Byte, Short> getShorts();
	public HashMap<Byte, Double> getDoubles();
	/**
	 * actually parses the script
	 * @param script
	 * @throws IOException
	 */
	public void parseScript(File script) throws IOException;
}
