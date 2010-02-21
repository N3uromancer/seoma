package network.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import network.Operation;

/**
 * a worker thread for the server, receives data from
 * the client it is assigned to
 * @author Jack
 *
 */
public final class ServerReceiverThread implements Runnable
{
	private Socket s;
	private Server server;
	
	/**
	 * creates a new receiver thread to listen for data from the
	 * client input stream
	 * @param s
	 * @param server a reference to the server to forward the information
	 * received to other connected clients
	 */
	public ServerReceiverThread(Socket s, Server server)
	{
		this.s = s;
		this.server = server;
	}
	public void run()
	{
		try
		{
			DataInputStream dis = new DataInputStream(s.getInputStream());
			boolean disconnected = false;
			while(!disconnected)
			{
				//System.out.println("reading...");
				byte operation = dis.readByte();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				dos.write(operation);
				
				if(operation == Operation.objectPosUpdate)
				{
					long id = dis.readLong();
					int x = dis.readInt();
					int y = dis.readInt();
					
					//System.out.println(id+", "+x+", "+y);
					
					dos.writeLong(id);
					dos.writeInt(x);
					dos.writeInt(y);
					
					server.writeToAll(baos.toByteArray(), s);
				}
				else if(operation == Operation.clientDisconnect)
				{
					server.disconnect(s);
					disconnected = true;
					System.out.println("client disconnecting...");
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		System.out.println("terminating server receiver thread");
		System.out.println("--------------------------------------------------");
	}
}
