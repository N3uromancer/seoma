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

public final class ExecuteActions extends Operation
{
	private World w;
	
	public ExecuteActions(World w)
	{
		super(IOConstants.executeAction);
		this.w = w;
	}
	public void performOperation(ByteBuffer buff, Connection c)
	{
		byte length = buff.get();
		for(byte q = Byte.MIN_VALUE; q < length; q++)
		{
			short id = buff.getShort();
			byte actionCount = buff.get(); //the number of actions committed by the object
			for(byte i = Byte.MIN_VALUE; i < actionCount; i++)
			{
				byte dataLength = buff.get(); //length of the data buffer not including the actionID
				byte actionID = buff.get();
				byte[] data = new byte[dataLength];
				buff.get(data);
				w.queueNetworkObjAction(id, actionID, data);
			}
		}
	}
	/**
	 * compiles a data packet tailored to order a unit to execute a specific action,
	 * uesd by the client to notify the server of client actions as they happen
	 * @param u the unit executing the action
	 * @param actionID the action to be executed
	 * @param pertData the pertinant data required to execute the action
	 * @return returns the compiled data packet
	 */
	public static byte[] compileExeActionBuff(Unit u, byte actionID, byte[] pertData)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.executeAction);
			dos.write(Byte.MIN_VALUE+1); //number of network objects included
			dos.writeShort(u.getID());
			dos.write(Byte.MIN_VALUE+1); //actions committed
			dos.write(pertData.length);
			dos.write(actionID);
			dos.write(pertData);
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
	/**
	 * compiles a data packet tailored to order multiple units to execute various actions,
	 * used by the relevant set to compile a several action orders together
	 * @param objCount the number of network objects included in the action data
	 * @param actionData the actual data for each object included in the packet
	 * @return returns the compiled data packet
	 */
	public static byte[] compileExeActionBuff(byte objCount, byte[] actionData)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			baos.write(IOConstants.executeAction);
			baos.write(objCount);
			baos.write(actionData);
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
