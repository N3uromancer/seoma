package network.operationExecutor;

import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * represents an operation to be executed by the operation executor,
 * each operation must be registered with an operation executor
 * in order to be carried out
 * @author Jack
 *
 */
public abstract class Operation
{
	private byte id;
	/**
	 * creates a new operation with the specified id
	 * @param id
	 */
	public Operation(byte id)
	{
		this.id = id;
	}
	/**
	 * gets the id for this operation
	 * @return returns the id
	 */
	public byte getID()
	{
		return id;
	}
	/**
	 * actually performs the operation, called when this
	 * operation's id is encountered in a packet
	 * @param buff the byte buffer containing the data
	 * for the operation
	 * @param address the address of the client that sent the packet
	 * containing the operation to be executed
	 */
	public abstract void performOperation(ByteBuffer buff, InetAddress address);
}
