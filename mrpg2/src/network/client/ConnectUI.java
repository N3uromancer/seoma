package network.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import world.WorldManager;
import account.Account;

import com.sun.sgs.client.simple.SimpleClient;

public class ConnectUI extends JFrame
{
	private static final long serialVersionUID = 1L;
	JTextField ip = new JTextField(15);
	/**
	 * sends the start message to the server when pressed, only
	 * enabled if this player is the host
	 */
	JButton start = new JButton("Start");
	JLabel status = new JLabel("Not Connected");
	
	int maxPlayers = 8;
	Account[] a = new Account[maxPlayers];
	JPanel[] ad = new JPanel[maxPlayers]; //the displayed accounts
	
	public ConnectUI()
	{
		//setLayout(new FlowLayout());
		setLayout(new BorderLayout());
		
		start.setEnabled(false);
		start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				
			}
		});
		
		add(createConnectPanel(), BorderLayout.NORTH);
		
		add(createPlayerPanel(), BorderLayout.CENTER);
		//add(start, BorderLayout.SOUTH);
		
		add(status, BorderLayout.PAGE_END);
		
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	private JPanel createConnectPanel()
	{
		JPanel ipPanel = new JPanel();
		ipPanel.setLayout(new FlowLayout());
		ipPanel.add(new JLabel("Host IP:"));
		ipPanel.add(ip);
		JPanel connectPanel = new JPanel();
		connectPanel.setLayout(new FlowLayout());
		connectPanel.add(ipPanel);
		JButton connect = new JButton("Connect");
		connect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				WorldManager wm = new WorldManager();
				ClientListener listener = new ClientListener(wm);
				SimpleClient client = new SimpleClient(listener);
				Properties connectProps = new Properties();
				//connectProps.put("host", "localhost");
				connectProps.put("host", ip.getText());
				connectProps.put("port", "4567");


				try
				{
					System.out.print("connecting...");
					client.login(connectProps);
					if(client.isConnected())
					{
						System.out.println(" success!");
						System.out.println("player = "+listener.getPlayer());
						start.setEnabled(listener.getPlayer()==0);

						status.setText("Connected, Host = "+(listener.getPlayer()==0));
						status.validate();
					}
					else
					{
						System.out.println(" failed");
						status.setText("Connection Failed");
						status.validate();
					}
				}
				catch(IOException a){}
			}
		});
		connectPanel.add(connect);
		connectPanel.add(start);
		return connectPanel;
	}
	public static void main(String[] args)
	{
		new ConnectUI();
	}
	private JPanel createPlayerPanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		for(int i = 0; i < maxPlayers; i++)
		{
			ad[i] = new PlayerPanel(new Account("untitled"));
			p.add(ad[i]);
		}
		return p;
	}
}
class PlayerPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	public PlayerPanel(Account a)
	{
		JPanel stats = new JPanel();
		stats.setLayout(new FlowLayout());
		stats.add(new JLabel("Account: "+a.getAccountName()));
		stats.add(new JLabel("K/D: "+a.getKills()+" / "+a.getDeaths()));
		stats.add(new JLabel("W/L: "+a.getWins()+" / "+a.getLosses()));
		add(stats);
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}
}
