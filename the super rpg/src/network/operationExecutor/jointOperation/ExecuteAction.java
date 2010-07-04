package network.operationExecutor.jointOperation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.Operation;
import world.World;
import world.unit.Unit;

public final class ExecuteAction extends Operation
{
	private World w;
	
	public ExecuteAction(World w)
	{
		super(IOConstants.executeAction);
		this.w = w;
	}
	public void performOperation(ByteBuffer buff, Connection c)
	{
		short id = buff.getShort();
		byte action = buff.get();
		byte length = buff.get(); //length of the data buffer
		byte[] data = new byte[length];
		buff.get(data);
	}
	/**
	 * compiles a data packet tailored to order a unit to execute a specific action
	 * @param u the unit executing the action
	 * @param action the action to be executed
	 * @param pertData the pertinant data required to execute the action
	 * @return returns the compiled data packet
	 */
	public static byte[] compileExeActionBuff(Unit u, byte action, byte[] pertData)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.executeAction);
			dos.writeShort(u.getID());
			dos.write(action);
			dos.write(pertData.length);
			dos.write(pertData);
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
