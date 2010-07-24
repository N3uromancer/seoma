package network;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import network.operationExecutor.OperationExecutor;
import network.receiver.TCPReceiver;
import network.writeController.WriteController;

/**
 * represents a connection to either the server or a client
 * @author Jack
 *
 */
public final class Connection
{
	private WriteController wc;
	private TCPReceiver tcpReceiver;
	private Socket tcpSocket;
	
	/**
	 * creates a new connection, setting up both a means to write to the connection and a means
	 * to read data from the connection
	 * @param tcpSocket
	 * @param udpSocket
	 * @param port the port udp packets are to be sent to
	 * @param oe
	 * @param converters
	 */
	public Connection(Socket tcpSocket, DatagramSocket udpSocket, int port, OperationExecutor oe)
	{
		System.out.println("creating connection...");
		this.tcpSocket = tcpSocket;
		wc = new WriteController(tcpSocket, udpSocket, tcpSocket.getInetAddress(), port);
		tcpReceiver = new TCPReceiver(tcpSocket, oe, this);
		System.out.println("new connection established");
	}
	/**
	 * writes the passed byte buffer to the connection using the indicated method of data transfer
	 * @param buff
	 * @param reliable
	 */
	public void write(byte[] buff, boolean reliable)
	{
		wc.write(buff, reliable);
	}
	public void closeConnection()
	{
		System.out.println("closing connection...");
		tcpReceiver.interrupt();
		wc.interruptTCPWriteThread();
	}
	public InetAddress getAddress()
	{
		return tcpSocket.getInetAddress();
	}
	/**
	 * returns the number of bytes in the tcp write queue
	 * @return returns the number of bytes in the tcp write queue
	 */
	public int getTCPQueueSize()
	{
		return wc.getTCPQueueSize();
	}
}
