package starter.advancedStarter;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class AdvancedStarter
{
	JFrame[] screens;
	
	public AdvancedStarter()
	{
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		screens = new JFrame[environment.getScreenDevices().length];
		for(int i = 0; i < environment.getScreenDevices().length; i++)
		{
			final GraphicsDevice device = environment.getScreenDevices()[i];
			JFrame f = new JFrame(device.getDefaultConfiguration())
			{
				private static final long serialVersionUID = 1L;
				public void paint(Graphics g)
				{
					g.setColor(Color.black);
					g.fillRect(0, 0, getWidth(), getHeight());
					AffineTransform at = new AffineTransform();
					BufferedImage bi = getBackgroundImage();
					if(bi != null)
					{
						at.scale(getWidth()*1./bi.getWidth(), getHeight()*1./bi.getHeight());
						Graphics2D g2d = (Graphics2D)g;
						g2d.setTransform(at);
						g2d.drawImage(bi, 0, 0, null);
						System.out.println("here");
					}
					//paintAll(g);
					g.dispose();
				}
			};
			f.setSize(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
			//f.setUndecorated(true);
			f.setVisible(true);
			screens[i] = f;
		}
		
		screens[0].add(new PrimaryStartPanel());
		screens[0].addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent e)
			{
				if(e.getKeyChar() == KeyEvent.VK_ESCAPE)
				{
					System.exit(0);
				}
			}
		});
		screens[0].paintAll(screens[0].getGraphics());
	}
	private void drawScreen(JFrame screen)
	{
		Graphics g = screen.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, screen.getWidth(), screen.getHeight());
		AffineTransform at = new AffineTransform();
		BufferedImage bi = getBackgroundImage();
		if(bi != null)
		{
			at.scale(screen.getWidth()*1./bi.getWidth(), screen.getHeight()*1./bi.getHeight());
			Graphics2D g2d = (Graphics2D)g;
			g2d.setTransform(at);
			g2d.drawImage(bi, 0, 0, null);
			System.out.println("here");
		}
		g.dispose();
	}
	private BufferedImage getBackgroundImage()
	{
		File f = new File("backgroundImages"+System.getProperty("file.separator")+"test.png");
		try
		{
			return ImageIO.read(f);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args)
	{
		new AdvancedStarter();
	}
}
