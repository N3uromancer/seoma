package network.operationExecutor.jointOperation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

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
	 * @param origArgs true if the original arguments for the initializables should be sent
	 * @return returns a byte buffer containing data for initialization orders
	 */
	public static byte[] createByteBuffer(ArrayList<Initializable> data, World w, boolean origArgs)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.performInitialization);
			dos.write(data.size()+Byte.MIN_VALUE);
			for(Initializable i: data)
			{
				byte[] buff = origArgs? i.getOriginalIniArgs(): i.getIniArgs();
				//System.out.println(i.getIniArgs().length);
				dos.write(w.getInitializer().getInitializeID(i));
				dos.write(buff.length+Byte.MIN_VALUE); //data length
				dos.write(buff);
			}
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
