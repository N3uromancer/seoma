package world;

import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * a simple class that runs one world, updating it
 * @author Secondary
 *
 */
public class EnvironmentViewer implements Runnable
{
	private static final long serialVersionUID = 1L;
	Region w;
	BufferStrategy bs;
	int width;
	int height;
	DisplayMode dm;

	public EnvironmentViewer()
	{
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = environment.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();
		dm = device.getDisplayMode();
		JFrame frame = new JFrame(config);
		
		JFileChooser fc = new JFileChooser();
		int decision = fc.showDialog((Component)frame, "Open World");
		if(decision == JFileChooser.APPROVE_OPTION)
		{
			device.setFullScreenWindow(frame);
			frame.setIgnoreRepaint(true);
			frame.createBufferStrategy(2);
			bs = frame.getBufferStrategy();
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			width = frame.getWidth();
			height = frame.getHeight();
			
			File f = fc.getSelectedFile();
			w = new Region();
			new Thread(this).start();
		}
		else
		{
			System.exit(0);
		}
	}
	public void run()
	{
		long sleepTime = 30;
		long start = System.currentTimeMillis();
		long diff = 0;
		for(;;)
		{
			start = System.currentTimeMillis();
			w.updateRegion(sleepTime);
			w.drawRegion((Graphics2D)bs.getDrawGraphics(), dm);
			bs.show();
			diff = System.currentTimeMillis()-start;
			if(diff <= sleepTime)
			{
				try
				{
					Thread.sleep(sleepTime-diff);
				}
				catch(InterruptedException e){}
			}
		}
	}
	public static void main(String[] args)
	{
		new EnvironmentViewer();
	}
}
