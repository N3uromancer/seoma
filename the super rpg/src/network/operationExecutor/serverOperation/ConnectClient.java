package network.operationExecutor.serverOperation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.Operation;

/**
 * defines a server side operation for connecting a client to
 * the server and spawning an avatar for the client
 * @author Jack
 *
 */
public final class ConnectClient extends Operation
{	
	public ConnectClient()
	{
		super(IOConstants.connectClient);
	}
	public void performOperation(ByteBuffer buff, Connection c)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.writeByte(IOConstants.controllerSetup);
			dos.writeShort(493);
		}
		catch(IOException e){}
		c.write(baos.toByteArray(), true);
		System.out.println("client connect message received! sending controller setup data");
	}
}
