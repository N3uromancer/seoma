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
	public static final byte updateNetworkObjects = 4;
	public static final byte unitAttack = 5; //probably useless
	public static final byte spawnObject = 6; //probably useless
	public static final byte executeAction = 7; //probably useless
	public static final byte destroyObject = 8;
	public static final byte performInitialization = 9;
}
