package server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.BoxClient;

import com.sun.sgs.app.*;

/**
 * sends a message to all clients connected to the channel to update their games
 * @author Jack
 *
 */
public class UpdateClientGames implements Task, Serializable, ManagedObject
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BoxWorldServer.class.getName());

	ManagedReference<Channel> c;
	
	public UpdateClientGames(ManagedReference<Channel> c)
	{
		this.c = c;
	}
	public void run() throws Exception
	{
		//logger.log(Level.INFO, "client update call sent");
		
		ByteBuffer b = generateUpdateMessage();
		Channel c = this.c.get();
		Iterator<ClientSession> i = c.getSessions();
		while(i.hasNext())
		{
			c.send(i.next(), b);
		}
	}
	private ByteBuffer generateUpdateMessage()
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeByte(BoxClient.updateGame);
			
			return ByteBuffer.wrap(baos.toByteArray());
		}
		catch(IOException a){}
		return null;
	}
}
