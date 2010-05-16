package network;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import network.operationExecutor.OperationExecutor;
import network.receiver.TCPReceiver;
import network.receiver.UDPReceiver;
import network.receiver.tcpStreamConverter.TCPStreamConverter;
import network.writeController.WriteController;

/**
 * represents a connection to either the server or a client
 * @author Jack
 *
 */
public final class Connection
{
	private WriteController wc;
	private UDPReceiver udpReceiver;
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
	public Connection(Socket tcpSocket, DatagramSocket udpSocket, int port, OperationExecutor oe, TCPStreamConverter[] converters)
	{
		this.tcpSocket = tcpSocket;
		wc = new WriteController(tcpSocket, udpSocket, tcpSocket.getInetAddress(), port);
		udpReceiver = new UDPReceiver(udpSocket, oe, this);
		tcpReceiver = new TCPReceiver(tcpSocket, oe, converters, this);
		System.out.println("new connection created");
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
		System.out.println("closing connection");
		udpReceiver.interrupt();
		tcpReceiver.interrupt();
		wc.interruptTCPWriteThread();
	}
	public InetAddress getAddress()
	{
		return tcpSocket.getInetAddress();
	}
}
