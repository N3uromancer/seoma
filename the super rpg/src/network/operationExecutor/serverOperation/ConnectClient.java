package network.operationExecutor.serverOperation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.Operation;
import world.World;
import world.unit.UnitInitializer;

/**
 * defines a server side operation for connecting a client to
 * the server and spawning an avatar for the client
 * @author Jack
 *
 */
public final class ConnectClient extends Operation
{
	private World w;
	
	public ConnectClient(World w)
	{
		super(IOConstants.connectClient);
		this.w = w;
	}
	public void performOperation(ByteBuffer buff, Connection c)
	{
		System.out.println("client connect message received!");
		short[] l = {200, 200}; //where the avatar is to be spawned
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		short id = w.generateNewID();
		try
		{
			dos.writeByte(IOConstants.controllerSetup);
			dos.writeShort(id);
			dos.writeShort(l[0]);
			dos.writeShort(l[1]);
		}
		catch(IOException e){}
		c.write(baos.toByteArray(), true);
		System.out.println("sending controller setup data");
		
		w.initialize(UnitInitializer.createInitializer(Byte.MIN_VALUE, true, id, Byte.MIN_VALUE, l[0], l[1]));
		w.registerRelevantSet(id, c);
	}
}
