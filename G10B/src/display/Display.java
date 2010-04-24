package display;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import world.World;

public class Display
{
	private World w;
	private Camera c;
	private BufferStrategy bs;
	private DisplayMode dm;
	
	public Display(World w, Camera c)
	{
		this.w = w;
		this.c = c;
		
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = environment.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();

		dm = device.getDisplayMode();
		JFrame frame = new JFrame(config);
		frame.setResizable(false);
		frame.setUndecorated(true);
		device.setFullScreenWindow(frame);
		frame.createBufferStrategy(2);
		bs = frame.getBufferStrategy();
		
		frame.addKeyListener(c);
	}
	public DisplayMode getDisplayMode()
	{
		return dm;
	}
	public void displayWorld()
	{
		Graphics2D g = (Graphics2D)bs.getDrawGraphics();
		w.drawWorld(g, c, 1280, 800);
		bs.show();
		g.dispose();
	}
}
