package blockTest;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.sun.sgs.app.*;

/**
 * acts as a filter to messages received by the channel
 * @author Jack
 *
 */
public class BlockWorldChannelListener implements Serializable, ChannelListener
{
	public void receivedMessage(Channel channel, ClientSession session, ByteBuffer message)
	{
		channel.send(session, message); //simply forwards received information
	}
}
