package network.server;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import network.IOConstants;
import network.operationExecutor.Operation;
import world.World;

/**
 * an operation that updates the client's avatar server side
 * @author Jack
 *
 */
public class AvatarUpdateOperation extends Operation
{
	Server s;
	World w;
	
	public AvatarUpdateOperation(Server s, World w)
	{
		super(IOConstants.avatarUpdate);
		this.s = s;
		this.w = w;
	}
	public void performOperation(ByteBuffer buff, InetAddress address)
	{
		w.getObject(s.getAvatarID(address.getHostAddress())).readState(buff);
	}
}
