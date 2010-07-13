package network.receiver.tcpStreamConverter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.IOConstants;

public class ExecuteActionsConverter extends TCPStreamConverter
{
	public ExecuteActionsConverter()
	{
		super(IOConstants.executeAction);
	}
	public byte[] convertStream(DataInputStream dis)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.executeAction);
			byte length = dis.readByte();
			dos.write(length);
			for(byte q = Byte.MIN_VALUE; q < length; q++)
			{
				short id = dis.readShort();
				dos.writeShort(id);
				byte actionCount = dis.readByte(); //the number of actions committed by the object
				dos.write(actionCount);
				for(byte i = Byte.MIN_VALUE; i < actionCount; i++)
				{
					byte dataLength = dis.readByte(); //length of the data buffer not including the actionID
					dos.write(dataLength);
					byte actionID = dis.readByte();
					dos.write(actionID);
					byte[] data = new byte[dataLength];
					dis.read(data);
					dos.write(data);
				}
			}
			
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
