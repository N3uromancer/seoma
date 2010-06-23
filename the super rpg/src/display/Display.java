package display;

import java.awt.BasicStroke;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import world.World;
import world.controller.Controller;

/**
 * displays one screen at a time, new screens can be loaded in via
 * the loadScreen method at any time, screens are dislayed via the
 * displayScreen method
 * @author Jack
 *
 */
public class Display
{
	BufferStrategy bs;
	DisplayMode dm;
	World w;
	
	/**
	 * creates a new display
	 * @param w the world to display
	 * @param c the controller registered as an input listener for the display,
	 * null for no controller
	 */
	public Display(World w, Controller c)
	{
		this.w = w;
		
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = environment.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();
		dm = device.getDisplayMode();
		
		JFrame f = new JFrame(config);
		f.setUndecorated(true);
		f.setIgnoreRepaint(true);
		f.setResizable(false);
		
		device.setFullScreenWindow(f);
		f.createBufferStrategy(2);
		bs = f.getBufferStrategy();
		
		if(c != null)
		{
			System.out.println("input listeners added to the controller");
			f.addKeyListener(c);
			f.addMouseListener(c);
		}
	}
	public void drawWorld()
	{
		Graphics2D g = (Graphics2D)bs.getDrawGraphics();
		//g.setStroke(new BasicStroke(2));
		/*RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);*/
		//g.setRenderingHints(rh);
		
		w.drawWorld(g, dm);
		bs.show();
	}
}
