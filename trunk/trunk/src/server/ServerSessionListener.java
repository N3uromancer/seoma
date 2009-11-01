package server;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.ManagedReference;

/**
 * listens for events from the client sessions
 * @author Jack
 *
 */
public class ServerSessionListener implements ClientSessionListener, Serializable
{
	private static final long serialVersionUID = 1L;
	ServerDisplay sd;
	
	public ServerSessionListener(ServerDisplay sd, ManagedReference<Channel> input)
	{
		this.sd = sd;
	}
	public void disconnected(boolean willingly)
	{
		String s = "disconnected";
		if(!willingly)
		{
			s = "lost connection";
		}
		sd.setStatus("client "+s);
	}
	public void receivedMessage(ByteBuffer arg0)
	{
		
	}
}
