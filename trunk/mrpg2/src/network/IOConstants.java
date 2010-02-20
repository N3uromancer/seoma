package network;

/**
 * holds the various constants for operations, each message
 * is headed by an operation so that the clients and server
 * know how to handle the message when it is received
 * @author Jack
 *
 */
public final class IOConstants
{
	public final static byte serverClose = 0;
	public final static byte gameUpdate = 0;
}
