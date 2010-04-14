package network;

public class IOConstants
{
	public static final int serverPort = 18497;
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
	public static final byte avatarUpdate = 4;
}
