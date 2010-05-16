package network.receiver.tcpStreamConverter;

import java.io.DataInputStream;

/**
 * converts a stream read from a tcp socket input stream to
 * a byte buffer to be interpreted by the operation executor
 * @author Jack
 *
 */
public abstract class TCPStreamConverter
{
	private byte id;
	
	public TCPStreamConverter(byte id)
	{
		this.id = id;
	}
	/**
	 * converts the stream represented by the passed input stream into a byte
	 * buffer to be interpreted by the operation executor
	 * @param dis
	 * @return returns a byte buffer representing the operation that was written to
	 * the stream, the first byte of the buffer should still be the operation the
	 * byte buffer describes because it is passed verbatum to the operation executor
	 */
	public abstract byte[] convertStream(DataInputStream dis);
	public byte getID()
	{
		return id;
	}
}
