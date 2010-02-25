package network;

/**
 * contains the methods for writing various things to output streams
 * @author Jack
 *
 */
public final class Operation
{
	/**
	 * indicates that the message contains update information
	 * for a game object
	 */
	public final static byte objectPosUpdate = 0;
	/**
	 * indicates that the message contains information specifying
	 * an object to be removed from the world
	 */
	public final static byte objectRemove = 1;
	/**
	 * indicates that the message contains client disconnect information
	 */
	public final static byte clientDisconnect = 2;
	/**
	 * indicates that the message contains server disconnect information,
	 * a client can safely disconnect after it receives this message back
	 * from the server in respons to the client disconnect message that it sent
	 */
	public final static byte serverDisconnect = 2;
}
