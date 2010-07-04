package network.operationExecutor.clientOperation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import network.Connection;
import network.IOConstants;
import network.operationExecutor.Operation;
import world.World;
import world.networkUpdateable.NetworkUpdateable;
import world.unit.Unit;

public final class DestroyObject extends Operation
{
	private World w;
	
	public DestroyObject(World w)
	{
		super(IOConstants.destroyObject);
		this.w = w;
	}
	public void performOperation(ByteBuffer buff, Connection c)
	{
		byte length = buff.get();
		for(int i = Byte.MIN_VALUE; i < length; i++)
		{
			short id = buff.getShort();
			System.out.println("destroying id="+id);
			w.destroyObject(id);
		}
	}
	public static byte[] createByteBuffer(ArrayList<NetworkUpdateable> u, World w)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.destroyObject);
			dos.write(u.size()-128);
			for(NetworkUpdateable o: u)
			{
				if(o instanceof Unit)
				{
					dos.writeShort(o.getID());
				}
			}
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
