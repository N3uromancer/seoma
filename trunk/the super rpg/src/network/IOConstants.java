package network;

public class IOConstants
{
	/**
	 * the port the server uses for its operations,
	 * clients connect to this port
	 */
	public static final int serverPort = 18497;
	/**
	 * the port the clients use for their operations,
	 * the server connects to this port
	 */
	public static final int clientPort = 16592;
	
	/*
	 * do not use 0 as an operation, an operation id of 0 signifies
	 * the end of operations to the executor, bytes are defaultly set
	 * to 0 when creating the buffer to receive packets
	 */
	//operations:
	public static final byte updateWorld = 1;
	public static final byte connectClient = 2;
	public static final byte controllerSetup = 3;
	public static final byte updateObject = 4;
	public static final byte unitAttack = 5;
	
	/*
	 * the spawn orders are split up into several categories
	 * to allow 256 types for each category of objects
	 */
	public static final byte spawnUnit = 6;
	public static final byte spawnItem = 7;
	public static final byte destroyObject = 8;
}
