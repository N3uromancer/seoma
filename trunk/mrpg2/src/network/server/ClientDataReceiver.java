package network.server;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.sun.sgs.app.ClientSessionListener;

/**
 * listens for information sent from the client
 * @author Jack
 *
 */
public class ClientDataReceiver implements ClientSessionListener, Serializable
{
	private static final long serialVersionUID = 1L;

	public void disconnected(boolean freely)
	{
		
	}
	public void receivedMessage(ByteBuffer abbrg0)
	{
		
	}
}
