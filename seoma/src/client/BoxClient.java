package client;

import box.Box;
import java.awt.*;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.util.Properties;
import javax.swing.*;
import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.simple.SimpleClient;
import com.sun.sgs.client.simple.SimpleClientListener;

public class BoxClient extends JFrame implements SimpleClientListener
{
	private static final long serialVersionUID = 1L;
	
	public static final byte sendInput = 0;
	public static final byte createNewBox = 1;
	public static final byte updateGame = 2;
	
	JTextArea outputArea = new JTextArea();
	JLabel status;
	Display d;
	
	SimpleClient sc;
	static final String MESSAGE_CHARSET = "UTF-8";
	
	Box[] b = new Box[10];
	KeyManager km = new KeyManager();
	
	ClientChannel channel;
	
	public BoxClient(String title)
	{
		super(title);
		Container c = getContentPane();
		
		JPanel appPanel = new JPanel();
		appPanel.setFocusable(false);
		
		c.setLayout(new BorderLayout());
		appPanel.setLayout(new BorderLayout());
		
		outputArea.setEditable(false);
		outputArea.setFocusable(false);
		appPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
		
		c.add(appPanel, BorderLayout.NORTH);
		status = new JLabel("not started");
		c.add(status, BorderLayout.SOUTH);
		
		d = new Display(this);
		
		c.add(d, BorderLayout.CENTER);
		setSize(640, 480);
		//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		sc = new SimpleClient(this);
	}
	public BoxClientChannelListener joinedChannel(ClientChannel channel)
	{
		this.channel = channel;
		appendOutput("Joined to channel "+channel.getName());
		return new BoxClientChannelListener(this);
	}
	public Display getDisplay()
	{
		return d;
	}
	public static void main(String[] args)
	{
		new BoxClient("block test client").login();
	}
	protected void setStatus(String s)
	{
		appendOutput("Status Set: " + s);
		status.setText("Status: " + s);
	}
	public PasswordAuthentication getPasswordAuthentication()
	{
		String player = "guest-"+(int)(Math.random()*1000);
		setStatus("Logging in as " + player);
		String password = "guest";
		return new PasswordAuthentication(player, password.toCharArray());
	}
	public void appendOutput(String s)
	{
		outputArea.append(s+"\n");
	}
	public KeyManager getKeyManager()
	{
		return km;
	}
	private void login()
	{
		String host = "localhost";
		String port = "4567";
		
		try
		{
			Properties connectProps = new Properties();
			connectProps.put("host", host);
			connectProps.put("port", port);
			sc.login(connectProps);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			disconnected(false, e.getMessage());
		}
	}
	/**
	 * called when the listener logs in
	 */
	public void loggedIn()
	{
		setStatus("logged in!");
	}
	public void loginFailed(String reason)
	{
		setStatus("login failed, "+reason);
	}
	public void disconnected(boolean willingly, String reason)
	{
		setStatus("disconnected, "+reason);
	}
	/**
	 * the incomming message from the server
	 */
	public void receivedMessage(ByteBuffer bb)
	{
		appendOutput("received a message from the server");
	}
	public Box[] getUnits()
	{
		return b;
	}
	public void reconnected()
	{
		setStatus("reconnected");
	}
	public void reconnecting()
	{
		setStatus("reconnecting...");
	}
}
