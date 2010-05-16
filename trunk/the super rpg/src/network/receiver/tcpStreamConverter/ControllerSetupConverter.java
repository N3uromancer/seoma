package network.receiver.tcpStreamConverter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.IOConstants;

/**
 * defines a class to convert controller setup information to a byte buffer
 * @author Jack
 *
 */
public class ControllerSetupConverter extends TCPStreamConverter
{
	public ControllerSetupConverter()
	{
		super(IOConstants.controllerSetup);
	}
	public byte[] convertStream(DataInputStream dis)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(IOConstants.controllerSetup);
			short id = dis.readShort();
			dos.writeShort(id);
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
