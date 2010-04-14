package network.operationExecutor.clientOperation;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import network.IOConstants;
import network.client.Client;
import network.operationExecutor.Operation;
import world.World;
import world.controller.Controller;

/**
 * the operation for setting up the controller on clients
 * @author Jack
 *
 */
public class ControllerSetup extends Operation
{
	World w;
	Client client;
	boolean connected = false;
	
	public ControllerSetup(World w, Client client)
	{
		super(IOConstants.controllerSetup);
		this.w = w;
		this.client = client;
	}
	public void performOperation(ByteBuffer buff, InetAddress address)
	{
		if(!connected)
		{
			connected = true;
			short id = buff.getShort();
			Controller c = new Controller(w.getObject(id));
			w.setController(c);
			client.connected();
			System.out.println("client controller set, avatar id = "+id);
		}
	}
}
