package TCP;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCP_Server {
	private ServerSocket sock;
	
	private ArrayList<Socket> clients;
	
	private TCP_Server_Callbacks callbacks;
	
	public TCP_Server(int port, TCP_Server_Callbacks calls) throws IOException
	{
		sock = new ServerSocket(port);
		clients = new ArrayList<Socket>();
		callbacks = calls;

		new AcceptThread(this, callbacks);
	}
	
	public ServerSocket getServerSocket()
	{
		return sock;
	}
	
	//Returned list is read-only unless in ReceiveException handler
	public ArrayList<Socket> getClientList()
	{
		return clients;
	}
}

class AcceptThread extends Thread
{
	private ServerSocket sock;
	
	private ArrayList<Socket> clients;
	
	private TCP_Server_Callbacks callbacks;
	private TCP_Server serv;
	
	public AcceptThread(TCP_Server server,
						TCP_Server_Callbacks calls)
	{
		serv = server;
		clients = serv.getClientList();
		sock = serv.getServerSocket();
		callbacks = calls;

		start();
	}
	
	public void run()
	{
		while (!isInterrupted())
		{
			Socket client;
			try {
				client = sock.accept();
				
				if (client != null)
				{
					clients.add(client);
					callbacks.ClientConnected(clients.indexOf(client), client);
					new ReceiveThread(serv, client, callbacks);
				}
			} catch (IOException e) { 
				callbacks.ConnectException(e);
				return;
			}
		}
	}
}

class ReceiveThread extends Thread
{
	private Socket client;
	private TCP_Server_Callbacks callbacks;
	private TCP_Server serv;
	
	public ReceiveThread(TCP_Server server, Socket sock, TCP_Server_Callbacks calls)
	{
		client = sock;
		callbacks = calls;
		serv = server;
		
		start();
	}
	
	public void run()
	{
		InputStream in;
		
		try {
			in = client.getInputStream();
		} catch (IOException e1) {
			serv.getClientList().remove(client);
			callbacks.ReceiveException(e1);
			return;
		}
		
		for (;;)
		{
			byte[] buffer;
			
			try {
				if (in.available() == 0)
				{
					byte[] temp = new byte[1];
					in.read(temp);
					buffer = new byte[in.available()+1];
					buffer[0] = temp[0];
				
					if (buffer.length > 1)
						in.read(buffer, 1, buffer.length-1);
				}
				else
				{
					buffer = new byte[in.available()];
					in.read(buffer);
				}
				int clientIndex = serv.getClientList().indexOf(client);
				if (clientIndex == -1) return;
				
				callbacks.DataReceived(clientIndex, buffer);
			} catch (IOException e) {
				serv.getClientList().remove(client);
				callbacks.ReceiveException(e);
				return;
			}
		}
	}
}
