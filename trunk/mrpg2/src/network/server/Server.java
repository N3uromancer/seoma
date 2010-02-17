package network.server;

import java.io.Serializable;
import java.util.Properties;

import network.DataPacket;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.Delivery;
import com.sun.sgs.app.util.ScalableList;

/**
 * represents the game server, all clients that connect are initially
 * joined to the "all" channel, updates messages are sent on the "all" channel
 * @author Jack
 *
 */
public class Server implements AppListener, Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * the name for the channel that all clients connect to in order to receive
	 * the general update message
	 */
	public static final String allChannelName = "all";
	
	/**
	 * stores all user input data that was received by the server
	 */
	ScalableList<DataPacket> s;
	/**
	 * the binding name for the list storing all user input data that is to
	 * be sent to the clients
	 */
	public static final String userInputData = "user input data";
	
	public void initialize(Properties p)
	{
		System.out.println("starting the server!");
		ChannelManager cm = AppContext.getChannelManager();
		cm.createChannel(allChannelName, null, Delivery.RELIABLE);

		AppContext.getTaskManager();
		
		//s = new ScalableList<DataPacket>();
		//AppContext.getDataManager().setBinding(userInputData, s);
		
		//AppContext.getTaskManager().schedulePeriodicTask(new UpdateTask(), 0, 30);
		
		new ServerDisplay(this);
	}
	/**
	 * issues the close message to clients and closes the server
	 */
	public void closeServer()
	{
		AppContext.getTaskManager();
		AppContext.getTaskManager().scheduleTask(new ServerCloseTask());
	}
	/**
	 * adds the connecting client session to the "all" channel and sets up its listener
	 */
	public ClientSessionListener loggedIn(ClientSession cs)
	{
		AppContext.getChannelManager().getChannel(allChannelName).join(cs);
		return new ClientDataReceiver();
	}
}
