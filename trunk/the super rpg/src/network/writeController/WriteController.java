package network.writeController;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * responsible for writing data to the server or to a single client
 * @author Jack
 *
 */
public final class WriteController
{
	private DatagramSocket udpSocket;
	private int port;
	private TCPWriteThread tcpWriter;
	private InetAddress address;
	
	public WriteController(Socket tcpSocket, DatagramSocket socket, InetAddress address, int port)
	{
		tcpWriter = new TCPWriteThread(tcpSocket);
		this.udpSocket = socket;
		this.address = address;
		this.port = port;
	}
	/**
	 * writes the passed byte buffer to the specified address
	 * @param buff
	 * @param reliable true if the data is to be written reliably, reliable data
	 * is sent via TCP, unreliable via UDP
	 */
	public void write(byte[] buff, boolean reliable)
	{
		if(!reliable)
		{
			new UDPWriteThread(buff, udpSocket, address, port);
		}
		else
		{
			tcpWriter.queueData(buff);
		}
	}
	public void interruptTCPWriteThread()
	{
		tcpWriter.interrupt();
	}
	/**
	 * returns the number of bytes in the tcp write queue
	 * @return returns the number of bytes in the tcp write queue
	 */
	public int getTCPQueueSize()
	{
		return tcpWriter.getQueueSize();
	}
}
