package network.client;

import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClientListener;

public class ClientListener implements SimpleClientListener
{
	public PasswordAuthentication getPasswordAuthentication()
	{
		System.out.println("password authentication queried, null returned");
		return null;
	}
	public void loggedIn()
	{
		System.out.println("logged in");
	}
	public void loginFailed(String s)
	{
		System.out.println("login failed, "+s);
	}
	public void disconnected(boolean arg0, String arg1)
	{
		System.out.println("disconnected");
	}
	public ClientChannelListener joinedChannel(ClientChannel cc)
	{
		System.out.println("joined a channel, "+cc.getName());
		return null;
	}
	public void receivedMessage(ByteBuffer arg0)
	{
		System.out.println("message received");
	}
	public void reconnected()
	{
		System.out.println("reconnected");
	}
	public void reconnecting()
	{
		System.out.println("reconnecting...");
	}
}
