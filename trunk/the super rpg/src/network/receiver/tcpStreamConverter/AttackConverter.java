package network.receiver.tcpStreamConverter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.IOConstants;

/**
 * converts attack commands into byte buffers
 * @author Jack
 *
 */
public class AttackConverter extends TCPStreamConverter
{
	public AttackConverter()
	{
		super(IOConstants.unitAttack);
	}
	public byte[] convertStream(DataInputStream dis)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			DataOutputStream dos = new DataOutputStream(baos);
			dos.write(IOConstants.unitAttack);
			byte length = dis.readByte();
			dos.write(length);
			for(byte i = 0; i < length; i++)
			{
				short id = dis.readShort();
				byte direction = dis.readByte();
				dos.writeShort(id);
				dos.write(direction);
			}
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
