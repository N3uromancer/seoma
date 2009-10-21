package blockTest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;
import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClient;
import com.sun.sgs.client.simple.SimpleClientListener;

public class Client extends JFrame implements SimpleClientListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	
	JTextField input = new JTextField();
	JTextArea outputArea = new JTextArea();
	JLabel status;
	
	SimpleClient sc;
	static final String MESSAGE_CHARSET = "UTF-8";
	
	private final HashMap<String, ClientChannel> channelsByName = new HashMap<String, ClientChannel>();
	JComboBox channelSelector;
	DefaultComboBoxModel channelSelectorModel;
	final static AtomicInteger channelNumberSequence = new AtomicInteger(1);
	
	public Client(String title)
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
		input.addActionListener(this);
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		populateInputPanel(inputPanel);
		inputPanel.setEnabled(true);
		appPanel.add(inputPanel, BorderLayout.SOUTH);
		
		c.add(appPanel, BorderLayout.CENTER);
		status = new JLabel("not started");
		c.add(status, BorderLayout.SOUTH);
		
		setSize(640, 480);
		//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		sc = new SimpleClient(this);
	}
	protected void populateInputPanel(JPanel panel)
	{
		panel.add(input, BorderLayout.CENTER);
		channelSelectorModel = new DefaultComboBoxModel();
		channelSelectorModel.addElement("<DIRECT>");
		channelSelector = new JComboBox(channelSelectorModel);
		channelSelector.setFocusable(false);
		panel.add(channelSelector, BorderLayout.WEST);
	}
	public BlockWorldClientChannelListener joinedChannel(ClientChannel channel)
	{
		String channelName = channel.getName();
		channelsByName.put(channelName, channel);
		appendOutput("Joined to channel "+channelName);
		channelSelectorModel.addElement(channelName);
		return new BlockWorldClientChannelListener(this);
	}
	public static void main(String[] args)
	{
		new Client("block test client").login();
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
	private void appendOutput(String s)
	{
		outputArea.append(s+"\n");
	}
	private void login()
	{
		//String host = System.getProperty(HOST_PROPERTY, DEFAULT_HOST);
		//String port = System.getProperty(PORT_PROPERTY, DEFAULT_PORT);
		
		String host = "QAYPN";
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
	private static ByteBuffer encodeString(String s)
	{
		try
		{
			return ByteBuffer.wrap(s.getBytes(MESSAGE_CHARSET));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new Error("Required character set "+MESSAGE_CHARSET+" not found", e);
		}
	}
	private static String decodeString(ByteBuffer buf)
	{
		try
		{
			byte[] bytes = new byte[buf.remaining()];
			buf.get(bytes);
			return new String(bytes, MESSAGE_CHARSET);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new Error("Required character set "+MESSAGE_CHARSET+" not found", e);
		}
	}
	/**
	 * called when the listener logs in
	 */
	public void loggedIn()
	{
		input.setEnabled(true);
		setStatus("logged in!");
	}
	public void loginFailed(String reason)
	{
		setStatus("login failed, "+reason);
	}
	public void disconnected(boolean willingly, String reason)
	{
		input.setEditable(false);
		setStatus("disconnected, "+reason);
	}
	public void receivedMessage(ByteBuffer message)
	{
		appendOutput("server sent: "+decodeString(message));
	}
	public void reconnected()
	{
		setStatus("reconnected");
	}
	public void reconnecting()
	{
		setStatus("reconnecting...");
	}
	public void actionPerformed(ActionEvent arg0)
	{
		if(sc.isConnected())
		{
			String text = input.getText();
			String channelName = (String)channelSelector.getSelectedItem();
			
			input.setText("");
			if (channelName.equalsIgnoreCase("<DIRECT>"))
			{
				send(text); //connected directly
			}
			else
			{
				//in a channel
				ClientChannel channel = channelsByName.get(channelName);
				try
				{
					channel.send(encodeString(text));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	private void send(String text)
	{
		try
		{
			ByteBuffer message = encodeString(text);
			sc.send(message);
			appendOutput("message: "+text+" sent");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	private static class BlockWorldClientChannelListener implements ClientChannelListener
	{
		private final int channelNum;
		Client c;
		
		public BlockWorldClientChannelListener(Client c)
		{
			channelNum = channelNumberSequence.getAndIncrement();
			this.c = c;
		}
		/**
		 * called when the server removes this listener from the channel
		 */
		public void leftChannel(ClientChannel channel)
		{
			c.appendOutput("Removed from channel " + channel.getName());
		}
		/**
		 * called when the server receives the message
		 */
		public void receivedMessage(ClientChannel channel, ByteBuffer message)
		{
			c.appendOutput("["+channel.getName()+"/ "+channelNum+"] "+decodeString(message));
		}
	}
}
