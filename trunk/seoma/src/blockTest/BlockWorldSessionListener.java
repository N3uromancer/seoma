package blockTest;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.*;

/**
 * a class representing the connection type to be sent to the clients
 * when they connect to the server
 * @author Jack
 *
 */
public class BlockWorldSessionListener implements ClientSessionListener, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BlockWorldSessionListener.class.getName());
	
	private final ManagedReference<ClientSession> sesRef; //the session the class is listening to
	private final String sesName; //the name of the client session for this listener
	
	public BlockWorldSessionListener(ClientSession cses, ManagedReference<Channel> channel)
	{
		sesRef = AppContext.getDataManager().createReference(cses);
		sesName = cses.getName();
		
		channel.get().join(cses);
	}
	public void disconnected(boolean disconnected)
	{
		String logOutType = disconnected ? "disconnected" : "lost connection";
		logger.log(Level.INFO, "client "+logOutType, new Object[]{sesName, logOutType});
	}
	public void receivedMessage(ByteBuffer bb)
	{
		logger.log(Level.INFO, "message received from client", sesName);
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
