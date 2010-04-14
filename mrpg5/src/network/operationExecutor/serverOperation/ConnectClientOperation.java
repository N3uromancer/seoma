package network.operationExecutor.serverOperation;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;

import network.IOConstants;
import network.operationExecutor.Operation;
import network.server.Server;

/**
 * defines a server side operation for connecting a client to
 * the server and spawning an avatar for the client
 * @author Jack
 *
 */
public final class ConnectClientOperation extends Operation
{
	/**
	 * contains the ip addresses of connected clients
	 */
	private HashSet<String> connected = new HashSet<String>();
	private Server s;
	
	public ConnectClientOperation(Server s)
	{
		super(IOConstants.connectClient);
		this.s = s;
	}
	public void performOperation(ByteBuffer buff, InetAddress address)
	{
		if(!connected.contains(address.getHostAddress()))
		{
			System.out.println("connected new client... ");
			connected.add(address.getHostAddress());
			s.connectClient(address);
			System.out.println("ip = "+address.getHostAddress());
		}
	}
}
