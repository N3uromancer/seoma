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
public class CreateNewBox implements Task, Serializable, ManagedObject
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BoxWorldServer.class.getName());

	ManagedReference<Channel> c;
	byte owner;
	
	public CreateNewBox(ManagedReference<Channel> c, byte owner)
	{
		this.c = c;
		this.owner = owner;
	}
	public void run() throws Exception
	{
		logger.log(Level.INFO, "creating new unit, owner = "+owner);
		
		ByteBuffer b = generateCreateNewClientMessage(owner);
		Channel c = this.c.get();
		Iterator<ClientSession> i = c.getSessions();
		while(i.hasNext())
		{
			logger.log(Level.INFO, "new unit creation order sent");
			c.send(i.next(), b);
		}
	}
	private ByteBuffer generateCreateNewClientMessage(byte owner)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeByte(BoxClient.createNewBox);
			dos.writeByte(owner);
			dos.writeDouble(50);
			dos.writeDouble(50);
			
			return ByteBuffer.wrap(baos.toByteArray());
		}
		catch(IOException a){}
		return null;
	}
}
