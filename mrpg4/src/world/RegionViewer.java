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
 * a simple class that runs one region, updating it
 * @author Secondary
 *
 */
public class RegionViewer implements Runnable
{
	private static final long serialVersionUID = 1L;
	Region r;
	BufferStrategy bs;
	int width;
	int height;
	DisplayMode dm;

	public RegionViewer()
	{
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = environment.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();
		dm = device.getDisplayMode();
		JFrame frame = new JFrame(config);
		
		JFileChooser fc = new JFileChooser();
		int decision = fc.showDialog((Component)frame, "Open Region");
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
			r = new Region();
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
			r.updateRegion(sleepTime);
			r.drawRegion((Graphics2D)bs.getDrawGraphics(), dm);
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
		new RegionViewer();
	}
}
