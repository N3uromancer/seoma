package network.server;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelListener;
import com.sun.sgs.app.ClientSession;

/**
 * listeners for information on the "all" channel
 * @author Jack
 *
 */
public class AllChannelListener implements ChannelListener, Serializable
{
	private static final long serialVersionUID = 1L;

	public void receivedMessage(Channel arg0, ClientSession arg1, ByteBuffer arg2)
	{
		
	}
}
