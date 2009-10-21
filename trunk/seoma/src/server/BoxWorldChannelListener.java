package server;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.sun.sgs.app.*;

/**
 * acts as a filter to messages received by the channel
 * @author Jack
 *
 */
public class BoxWorldChannelListener implements Serializable, ChannelListener
{
	private static final long serialVersionUID = 1L;

	public void receivedMessage(Channel channel, ClientSession session, ByteBuffer message)
	{
		channel.send(session, message); //simply forwards received information
	}
}
