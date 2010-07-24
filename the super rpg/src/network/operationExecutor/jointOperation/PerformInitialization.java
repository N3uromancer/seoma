package network.operationExecutor.jointOperation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.Operation;
import world.World;
import world.initializer.Initializable;
import world.initializer.Initializer;

/**
 * reads initialization orders from packets, actual initialization is deffered to the
 * udpate method of the world so as not to slow the operation executer thread
 * @author Jack
 *
 */
public final class PerformInitialization extends Operation
{
	private World w;
	private Initializer initializer;
	
	public PerformInitialization(World w)
	{
		super(IOConstants.performInitialization);
		this.initializer = w.getInitializer();
		this.w = w;
	}
	public void performOperation(ByteBuffer buff, Connection c)
	{
		byte length = buff.get();
		for(byte i = Byte.MIN_VALUE; i < length; i++)
		{
			byte iniID = buff.get();
			byte dataLength = buff.get();
			byte[] data = new byte[dataLength-Byte.MIN_VALUE];
			buff.get(data);
			w.initialize(initializer.getInitializable(iniID, data));
		}
	}
	/**
	 * creates a byte buffer containing the pertinant information to give initialization
	 * orders to a client
	 * @param data
	 * @param w
	 * @return returns a byte buffer containing initialization order data
	 */
	public static byte[] createByteBuffer(HashMap<Initializable, byte[]> data, World w)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.performInitialization);
			dos.write(data.size()+Byte.MIN_VALUE);
			for(Initializable i: data.keySet())
			{
				dos.write(w.getInitializer().getInitializeID(i));
				dos.write(data.get(i).length+Byte.MIN_VALUE); //data length
				dos.write(data.get(i));
			}
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
