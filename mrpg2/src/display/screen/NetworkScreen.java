package display.screen;

import java.awt.Color;
import java.awt.DisplayMode;
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

public class NetworkScreen implements Screen
{
	JComponent[] c;
	BufferedImage background;
	Display d;
	
	public NetworkScreen(Display display)
	{
		c = new JComponent[2];
		d = display;
		
		
		JTextField ip = new JTextField(15);
		//ip.setBounds(20, 40, 70, 20);
		//c[0] = ip;
		
		JLabel ipLabel = new JLabel("Host IP:");
		//ipLabel.setBounds(20, 20, 70, 20);
		//c[1] = ipLabel;

		JPanel ipPanel = new JPanel();
		ipPanel.setLayout(new BoxLayout(ipPanel, BoxLayout.Y_AXIS));
		ipPanel.setBounds(20, 20, 200, 50);
		ipPanel.add(ipLabel);
		ipPanel.add(ip);
		c[0] = ipPanel;
		
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
