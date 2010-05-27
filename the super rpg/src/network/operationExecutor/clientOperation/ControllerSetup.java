package network.operationExecutor.clientOperation;

import java.nio.ByteBuffer;

import network.Connection;
import network.IOConstants;
import network.client.Client;
import network.operationExecutor.Operation;
import world.World;
import world.unit.Avatar;

/**
 * the operation for setting up the controller on clients
 * and creating the avatar for them to control
 * @author Jack
 *
 */
public class ControllerSetup extends Operation
{
	World w;
	Client client;
	
	public ControllerSetup(World w, Client client)
	{
		super(IOConstants.controllerSetup);
		this.w = w;
		this.client = client;
	}
	public void performOperation(ByteBuffer buff, Connection c)
	{
		short id = buff.getShort();
		Avatar a = new Avatar(true);
		
		
		System.out.println("client controller set, avatar id = "+id);
		/*Avatar a = new Avatar(false, new double[]{50, 50});
		a.readState(buff);
		w.registerObject((GameObject)a, id, (byte)0);
		
		Controller c = new Controller(w.getObject(id));
		w.setController(c);*/
		client.connected();
	}
}
