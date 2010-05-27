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

public final class Camera
{
	/**
	 * the location of top right corner of the camera
	 */
	private double[] l; //camera location, where it is centered
	private double zoom = 1;
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
	}
	public void translate(double[] t, double tdiff)
	{
		l[0]+=t[0]*tdiff/zoom;
		l[1]+=t[1]*tdiff/zoom;
	}
	public AffineTransform getTransform()
	{
		AffineTransform at = new AffineTransform();
		at.translate(-l[0]*zoom, l[1]*zoom);
		at.scale(zoom, zoom);
		return at;
	}
	public void zoom(double z)
	{
		//adjusts the location of the camera to keep the view centered while zooming
		double startx = dwidth/zoom;
		double starty = dheight/zoom;
		zoom = z; //zoom*=z.get(c);
		double xdiff = dwidth/zoom-startx;
		double ydiff = dheight/zoom-starty;
		l[0]-=xdiff/2;
		l[1]+=ydiff/2;
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
		//System.out.println("z="+zoom+", "+new Rectangle(l[0], -l[1], dwidth/zoom, dheight/zoom));
		return new Rectangle(l[0], -l[1], dwidth/zoom, dheight/zoom);
	}
}
