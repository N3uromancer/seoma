package geomUtil;

import geom.Boundable;
import geom.Rectangle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PartitionDemonstrator implements MouseMotionListener, MouseListener, Runnable
{
	Rectangle[] r;
	JPanel display;
	PartitionManager pm;
	HashSet<Boundable> intersects; //contains rectangles that were intersected by the mouse drag
	Semaphore intSem = new Semaphore(1, true);
	
	//mouse location
	int[] start = null;
	int[] end = null;
	
	public PartitionDemonstrator(int rectangles)
	{
		r = new Rectangle[rectangles];
		
		int width = 900;
		int height = 600;
		pm = new PartitionManager(0, 0, width, height, 10, 30, 50);
		
		double maxRectWidth = 70;
		double maxRectHeight = 50;
		for(int i = 0; i < rectangles; i++)
		{
			r[i] = new Rectangle(Math.random()*width, Math.random()*height, Math.random()*maxRectWidth, Math.random()*maxRectHeight);
			pm.add(r[i]);
		}
		
		JFrame f = new JFrame();
		display = new JPanel()
		{
			private static final long serialVersionUID = 1L;
			public void update(Graphics g)
			{
				paint(g);
			}
			public void paint(Graphics graphics)
			{
				Graphics2D g = (Graphics2D)graphics;
				g.setColor(Color.green);
				g.fillRect(0, 0, getWidth(), getHeight());
				if(start != null && end != null)
				{
					g.setColor(Color.LIGHT_GRAY);
					g.fillRect(start[0], start[1], end[0]-start[0], end[1]-start[1]);
				}
				for(Rectangle rectangle: r)
				{
					g.setColor(Color.black);
					if(intersects != null)
					{
						try
						{
							intSem.acquire();
							if(intersects.contains(rectangle))
							{
								g.setColor(Color.red);
							}
							intSem.release();
						}
						catch(InterruptedException e){}
					}
					double[] l = rectangle.getLocation();
					g.drawRect((int)l[0], (int)l[1], (int)rectangle.getWidth(), (int)rectangle.getHeight());
				}
			}
		};
		f.setSize(width, height);
		f.add(display);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.addMouseListener(this);
		f.addMouseMotionListener(this);
		f.setVisible(true);
		new Thread(this).start();
	}
	public void run()
	{
		for(;;)
		{
			display.repaint();
			try
			{
				Thread.sleep(2);
			}
			catch(InterruptedException e){}
		}
	}
	public static void main(String[] args)
	{
		new PartitionDemonstrator(50);
	}
	public void mouseMoved(MouseEvent arg0){}
	public void mouseClicked(MouseEvent arg0){}
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}
	public void mousePressed(MouseEvent e)
	{
		start = new int[]{e.getPoint().x, e.getPoint().y};
		try
		{
			intSem.acquire();
			intersects = null;
			intSem.release();
		}
		catch(InterruptedException a){}
	}
	public void mouseReleased(MouseEvent e)
	{
		end = new int[]{e.getPoint().x, e.getPoint().y};
		try
		{
			intSem.acquire();
			intersects = pm.intersects(start[0], start[1], end[0]-start[0], end[1]-start[1]);
			intSem.release();
		}
		catch(InterruptedException a){}
	}
	public void mouseDragged(MouseEvent e)
	{
		end = new int[]{e.getPoint().x, e.getPoint().y};
	}
}
