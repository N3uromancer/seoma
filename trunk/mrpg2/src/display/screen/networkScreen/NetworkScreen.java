package display.screen.networkScreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import display.Display;
import display.screen.HomeScreen;
import display.screen.Screen;
import display.screen.ScreenConstants;

public class NetworkScreen implements Screen
{
	JComponent[] c;
	BufferedImage background;
	Display d;
	
	public NetworkScreen(Display display)
	{
		c = new JComponent[2];
		d = display;
		
		createConnectPanel(0);
		
		JButton back = new JButton("Back");
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				d.loadScreen(new HomeScreen(d));
				d.updateDisplay();
			}
		});
		back.setBounds(ScreenConstants.backButtonLocation[0], ScreenConstants.backButtonLocation[1], 200, 40);
		c[1] = back;
		
		
		try
		{
			File f = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+
					"images"+System.getProperty("file.separator")+"world map.bmp");
			background = ImageIO.read(f);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * creates the panel that displays the other players connected to the server
	 * @param index
	 */
	private void createGameSessionPanel(int index)
	{
		JPanel sessionPanel = new JPanel();
	}
	/**
	 * sets up and adds the panel for connecting to the game
	 * @param index the index to which the connection panel is added to
	 * in the array of components associated with this screen
	 */
	private void createConnectPanel(int index)
	{
		final JPanel connectPanel = new JPanel();
		
		final JTextField ipField = new JTextField(15);
		JLabel ipLabel = new JLabel("Host IP:");
		JPanel ipGroup = new JPanel();
		ipGroup.setLayout(new FlowLayout());
		ipGroup.add(ipLabel);
		ipGroup.add(ipField);
		ipGroup.setPreferredSize(new Dimension(200, 30));
		
		final JButton connect = new JButton("Connect");
		connect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("connecting to "+ipField.getText()+"...");
				
				ipField.setEditable(false);
				connect.setEnabled(false);
			}
		});

		connectPanel.setLayout(new BoxLayout(connectPanel, BoxLayout.Y_AXIS));
		connectPanel.add(ipGroup);
		connectPanel.add(connect);
		connectPanel.setBounds(20, 20, 250, 60);
		//ipPanel.setBounds(20, 20, 500, 500);
		c[index] = connectPanel;
	}
	public void displayScreen(Graphics2D g, DisplayMode dm)
	{
		if(background != null)
		{
			g.drawImage(background, 0, 0, null);
		}
		else
		{
			g.setColor(Color.white);
			g.fillRect(0, 0, dm.getWidth(), dm.getHeight());
		}
	}
	public JComponent[] getComponents()
	{
		return c;
	}
}
