package TCP;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCP_Client implements Runnable {
	private Socket sock;
	
	private TCP_Client_Callbacks callbacks;
	
	public TCP_Client(InetAddress addr, int port, TCP_Client_Callbacks calls) throws IOException
	{
		sock = new Socket(addr, port);
		callbacks = calls;
		new Thread(this).start();
	}
	
	public Socket getSocket()
	{
		return sock;
	}
	
	public void run()
	{
		for (;;)
		{
			byte[] buffer;
			InputStream in;
			try {
				in = sock.getInputStream();
				
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
				
				callbacks.DataReceived(buffer);
			} catch (IOException e) {
				callbacks.ReceiveException(e);
				return;
			}
		}	
	}
}
