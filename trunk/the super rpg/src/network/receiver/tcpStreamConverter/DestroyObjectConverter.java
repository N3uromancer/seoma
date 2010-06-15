package network.receiver.tcpStreamConverter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.IOConstants;

public class DestroyObjectConverter extends TCPStreamConverter
{
	public DestroyObjectConverter()
	{
		super(IOConstants.destroyObject);
	}
	public byte[] convertStream(DataInputStream dis)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.destroyObject);
			byte length = dis.readByte();
			dos.write(length);
			
			for(int i = Byte.MIN_VALUE; i < length; i++)
			{
				short id = dis.readShort();
				dos.writeShort(id);
			}
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
