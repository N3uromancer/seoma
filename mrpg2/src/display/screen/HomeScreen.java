package display.screen;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * the starting screen displayed when the game starts
 * @author Jack
 *
 */
public class HomeScreen implements Screen
{
	JComponent[] c;
	
	BufferedImage background;
	BufferedImage adventurePic;
	
	public HomeScreen()
	{
		c = new JComponent[5];
		
		ImageButton adventure = new ImageButton(new File(System.getProperty("user.dir")+
				System.getProperty("file.separator")+"images"+System.getProperty("file.separator")+"adventure.bmp"));
		adventure.setBounds(1000, 200, 200, 40);
		adventure.setToolTipText("Single Player Adventure!");
		c[0] = adventure;
		
		ImageButton network = new ImageButton(new File(System.getProperty("user.dir")+
				System.getProperty("file.separator")+"images"+System.getProperty("file.separator")+"network.bmp"));
		network.setBounds(1000, 250, 200, 40);
		network.setToolTipText("Network Game!");
		c[1] = network;
		
		c[2] = new JButton("Account");
		c[2].setBounds(1000, 300, 200, 40);
		
		c[3] = new JButton("About");
		c[3].setBounds(1000, 350, 200, 40);
		
		JButton exit = new JButton("Exit");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		exit.setBounds(1000, 600, 200, 40);
		c[4] = exit;
		
		try
		{
			File f = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+
					"images"+System.getProperty("file.separator")+"stone path.bmp");
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
		
		/*AffineTransform at = new AffineTransform();
		at.translate(50, 50);
		g.setTransform(at);
		c[0].paint(g);*/
	}
	public JComponent[] getComponents()
	{
		return c;
	}
}
