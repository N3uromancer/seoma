package display;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import javax.swing.JComponent;
import javax.swing.JFrame;

import display.screen.Screen;

/**
 * displays one screen at a time, new screens can be loaded in via
 * the loadScreen method at any time, screens are dislayed via the
 * displayScreen method
 * @author Jack
 *
 */
public class Display
{
	private static final long serialVersionUID = 1L;
	JFrame f;
	BufferStrategy bs;
	DisplayMode dm;
	/**
	 * the screen currently loaded and being displayed
	 */
	Screen s;
	
	public Display()
	{
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = environment.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();
		dm = device.getDisplayMode();
		
		f = new JFrame(config);
		
		f.setUndecorated(true);
		f.setIgnoreRepaint(true);
		device.setFullScreenWindow(f);
		f.createBufferStrategy(2);
		bs = f.getBufferStrategy();
		f.setResizable(false);
		f.setLayout(null);
		
		f.addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent e)
			{
				System.exit(0);
			}
		});
		f.getRootPane().addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e)
			{
				f.requestFocus();
			}
		});
	}
	public JFrame getJFrame()
	{
		return f;
	}
	/**
	 * loads a screen to be displayed by every call to the update display
	 * @param s
	 */
	public void loadScreen(Screen screen)
	{
		//System.out.println("loading screen...");
		if(s != null)
		{
			//System.out.print("removing old components... ");
			for(JComponent c: s.getComponents())
			{
				f.remove(c);
			}
			//System.out.println("done!");
		}
		s = screen;
		//System.out.println("adding new components...");
		for(JComponent c: s.getComponents())
		{
			//System.out.println(c.getClass().getSimpleName()+" added");
			f.add(c);
		}
		//System.out.println("done!");
	}
	public void updateDisplay()
	{
		Graphics2D g = (Graphics2D)bs.getDrawGraphics();
		s.displayScreen(g, dm);
		for(JComponent c: s.getComponents())
		{
			AffineTransform at = new AffineTransform();
			at.translate(c.getLocation().x, c.getLocation().y);
			g.setTransform(at);
			c.paintAll(g);
			//c.paint(g);
		}
		bs.show();
	}
}
