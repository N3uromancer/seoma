package network.receiver.tcpStreamConverter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.IOConstants;

public class SpawnObjectConverter extends TCPStreamConverter
{
	public SpawnObjectConverter()
	{
		super(IOConstants.spawnObject);
	}
	public byte[] convertStream(DataInputStream dis)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.spawnObject);
			byte length = dis.readByte();
			dos.write(length);
			
			for(int i = Byte.MIN_VALUE; i < length; i++)
			{
				byte type = dis.readByte();
				byte regionID = dis.readByte();
				short id = dis.readShort();
				
				byte iniLength = dis.readByte();
				byte[] iniState = new byte[iniLength];
				dis.read(iniState, 0, iniLength);
				
				dos.write(type);
				dos.write(regionID);
				dos.writeShort(id);
				
				dos.write(iniLength);
				dos.write(iniState);
			}
		}
		catch(IOException e){}
		return baos.toByteArray();
	}

}
