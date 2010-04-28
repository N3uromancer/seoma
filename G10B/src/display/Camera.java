package display;

import geom.Rectangle;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

public final class Camera implements KeyListener, MouseListener
{
	private double[] l; //camera location, where it is centered
	private HashSet<Character> down = new HashSet<Character>();
	private Semaphore downSem = new Semaphore(1, true);
	private HashMap<Character, double[]> t = new HashMap<Character, double[]>();
	private int dwidth;
	private int dheight;
	
	/**
	 * creates a new camera
	 * @param l the location of the center of the camera
	 * @param dwidth the width of the display area
	 * @param dheight the height of the display area
	 */
	public Camera(double[] l, int dwidth, int dheight)
	{
		this.l = l;
		this.dwidth = dwidth;
		this.dheight = dheight;
		
		double m = 500;
		t.put('w', new double[]{0, m});
		t.put('d', new double[]{m, 0});
		t.put('s', new double[]{0, -m});
		t.put('a', new double[]{-m, 0});
	}
	private void translate(double[] t, double tdiff)
	{
		l[0]+=t[0]*tdiff;
		l[1]+=t[1]*tdiff;
	}
	public AffineTransform getTransform()
	{
		AffineTransform at = new AffineTransform();
		at.translate(-l[0], l[1]);
		return at;
	}
	public void updateCamera(double tdiff)
	{
		for(char c: down)
		{
			translate(t.get(c), tdiff);
		}
	}
	public void keyPressed(KeyEvent e)
	{
		if(t.keySet().contains(e.getKeyChar()))
		{
			try
			{
				downSem.acquire();
				down.add(e.getKeyChar());
				downSem.release();
			}
			catch(InterruptedException a){}
		}
	}
	public void keyReleased(KeyEvent e)
	{
		if(t.keySet().contains(e.getKeyChar()))
		{
			try
			{
				downSem.acquire();
				down.remove(e.getKeyChar());
				downSem.release();
			}
			catch(InterruptedException a){}
		}
	}
	public void keyTyped(KeyEvent e)
	{
		if(e.getKeyChar() == KeyEvent.VK_ESCAPE)
		{
			System.exit(0);
		}
	}
	/**
	 * sets the display bounds for the camera
	 * @param dwidth
	 * @param dheight
	 */
	public void setViewBounds(int dwidth, int dheight)
	{
		this.dwidth = dwidth;
		this.dheight = dheight;
	}
	/**
	 * returns a rectangle representing the game space area that is viewed
	 * by this camera
	 * @return returns the view bounds
	 */
	public Rectangle getViewBounds()
	{
		return new Rectangle(l[0]-dwidth/2, -l[1]-dheight/2, dwidth*2, dheight*2);
	}
	public void mouseClicked(MouseEvent arg0){}
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}
	public void mousePressed(MouseEvent e)
	{
		
	}
	public void mouseReleased(MouseEvent arg0){}
}
