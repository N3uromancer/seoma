package server;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.sgs.app.*;

/**
 * a class representing the connection type to be sent to the clients
 * when they connect to the server
 * @author Jack
 *
 */
public class BoxWorldSessionListener implements ClientSessionListener, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BoxWorldSessionListener.class.getName());
	
	private final ManagedReference<ClientSession> sesRef; //the session the class is listening to
	AtomicInteger memberCount; //the number of clients on the channel
	byte owner;
	
	public BoxWorldSessionListener(ClientSession cses, ManagedReference<Channel> channel, AtomicInteger memberCount)
	{
		byte o = (byte)memberCount.getAndIncrement();
		this.memberCount = memberCount;
		this.owner = memberCount.byteValue();
		sesRef = AppContext.getDataManager().createReference(cses);
		
		channel.get().join(cses);
		
		logger.log(Level.INFO, "sending new unit information...");
		//channel.get().send(cses, generateCreateNewClientMessage(o));
		TaskManager tm = AppContext.getTaskManager();
		tm.scheduleTask(new CreateNewBox(channel, o));
	}
	public void disconnected(boolean disconnected)
	{
		String logOutType = disconnected ? "disconnected" : "lost connection";
		int count = memberCount.decrementAndGet();
		logger.log(Level.INFO, "client "+logOutType+", "+count+" connected clients remaining");
	}
	public void receivedMessage(ByteBuffer bb)
	{
		logger.log(Level.INFO, "message received from client");
		ClientSession cs = getSession();
		cs.send(bb);
	}
	/**
	 * gets a copy of the session for this listener
	 * @return returns the session for this listener
	 */
	private ClientSession getSession()
	{
		return sesRef.get();
	}
}
