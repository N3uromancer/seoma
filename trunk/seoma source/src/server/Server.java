package server;

import java.io.Serializable;
import java.util.Properties;

import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.ManagedReference;

public class Server implements Serializable, AppListener
{
	private static final long serialVersionUID = 1L;
	ServerDisplay sd;
	
	ManagedReference<Channel> input; //the channel that handles the forwarding of user input
	
	public Server()
	{
		
	}
	public void initialize(Properties p)
	{
		
	}
	public ClientSessionListener loggedIn(ClientSession cs)
	{
		return new ServerSessionListener(sd, input);
	}
}
