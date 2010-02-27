package UDP;
import java.net.*;


public class UDP_Connection implements Runnable {
	private DatagramSocket dsock;
	private UDP_Callbacks callbacks;
	private int maxSize;
	private Thread thrd;
	
	public UDP_Connection(int prt, UDP_Callbacks calls, int maxPacketSize) throws SocketException
	{
		dsock = new DatagramSocket(prt);
		callbacks = calls;
		maxSize = maxPacketSize;
		
		//Create our receive thread and start it
		thrd = new Thread(this);
		thrd.start();
	}
	
	public void run()
	{
		while (!thrd.isInterrupted())
		{
			DatagramPacket pack = new DatagramPacket(new byte[maxSize], maxSize);
			
			try {
				//Receive the packet
				dsock.receive(pack);
				
				//If we receive without throwing an exception then forward
				//the packet to the PacketReceived handler
				callbacks.PacketReceived(pack);
				
			} catch (Exception e) {
				//Forward the exception to the ReceiveException handler
				//so it can determine what to do
				callbacks.ReceiveException(e);
				return;
			}
		}
	}
	
	//Returns the underlying datagram socket
	public DatagramSocket getSocket()
	{
		return dsock;
	}
}
