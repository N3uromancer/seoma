package network.receiver.tcpStreamConverter;

import java.io.DataInputStream;

import network.IOConstants;

public class ConnectClientConverter extends TCPStreamConverter
{
	public ConnectClientConverter()
	{
		super(IOConstants.connectClient);
	}
	public byte[] convertStream(DataInputStream dis)
	{
		return new byte[]{IOConstants.connectClient};
	}
}
