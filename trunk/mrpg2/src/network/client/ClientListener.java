package network.client;

import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;

import world.WorldManager;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClientListener;

/**
 * listens for client actions and updates from the server,
 * whenever the listener receives updates from the server it
 * updates the game state accordingly
 * @author Jack
 *
 */
public class ClientListener implements SimpleClientListener
{
	WorldManager wm;
	/**
	 * boolean for determining if the game has started, if false
	 * then still in the lobby, true then in a game
	 */
	boolean inGame = false;
	
	byte player = -1;
	
	public ClientListener(WorldManager w)
	{
		this.wm = w;
	}
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
		System.out.println("login failed: "+s);
	}
	public void disconnected(boolean gracefully, String s)
	{
		System.out.println("disconnected: "+s);
	}
	public ClientChannelListener joinedChannel(ClientChannel cc)
	{
		System.out.println("joined a channel: "+cc.getName());
		return null;
	}
	public void receivedMessage(ByteBuffer b)
	{
		System.out.println("message received");
		if(inGame)
		{
			double tdiff = b.getDouble();
			wm.update(tdiff);
		}
		else
		{
			if(player == -1)
			{
				player = b.get();
			}
		}
	}
	public byte getPlayer()
	{
		return player;
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
