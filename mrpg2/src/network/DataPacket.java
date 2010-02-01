package network;

public final class DataPacket
{
	private byte[] b;
	
	public DataPacket(byte[] b)
	{
		this.b = b;
	}
	public byte[] getData()
	{
		return b;
	}
}
