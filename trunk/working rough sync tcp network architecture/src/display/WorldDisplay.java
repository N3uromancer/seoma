package display;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import world.World;

/**
 * display a world, meant to be used client side to display
 * the players world
 * @author Jack
 *
 */
public class WorldDisplay
{
	World w;
	DisplayMode dm;
	BufferStrategy bs;
	
	public WorldDisplay(World w)
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
		
		f.addKeyListener(w);
	}
	/**
	 * updates the display, draws the world
	 */
	public void drawWorld()
	{
		Graphics2D g = (Graphics2D)bs.getDrawGraphics();
		w.displayWorld(g, dm);
		bs.show();
	}
}
