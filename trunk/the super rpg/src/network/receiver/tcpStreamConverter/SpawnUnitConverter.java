package network.receiver.tcpStreamConverter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.IOConstants;

public class SpawnUnitConverter extends TCPStreamConverter
{
	public SpawnUnitConverter()
	{
		super(IOConstants.spawnUnit);
	}
	public byte[] convertStream(DataInputStream dis)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.spawnUnit);
			byte length = dis.readByte();
			dos.write(length);
			
			for(int i = Byte.MIN_VALUE; i < length; i++)
			{
				byte type = dis.readByte();
				byte regionID = dis.readByte();
				short id = dis.readShort();
				
				dos.write(type);
				dos.write(regionID);
				dos.writeShort(id);
			}
		}
		catch(IOException e){}
		return baos.toByteArray();
	}

}
