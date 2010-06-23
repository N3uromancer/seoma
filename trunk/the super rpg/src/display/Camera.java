package display;

import geom.Rectangle;

import java.awt.geom.AffineTransform;

public final class Camera
{
	/**
	 * the location of top left corner of the camera view area
	 */
	private double[] l;
	private double zoom = 1;
	private int dwidth;
	private int dheight;
	
	/**
	 * creates a new camera
	 * @param l the location of the top left corner of the camera view area
	 * @param dwidth the width of the display area
	 * @param dheight the height of the display area
	 */
	public Camera(double[] l, int dwidth, int dheight)
	{
		this.l = l;
		this.dwidth = dwidth;
		this.dheight = dheight;
	}
	/**
	 * returns the zoom amount of the camera
	 * @return
	 */
	public double getZoom()
	{
		return zoom;
	}
	/**
	 * gets a copy of the location of the top left corner of the view area of the camera
	 * @return gets a copy of the location of the camera
	 */
	public double[] getLocation()
	{
		return new double[]{l[0], l[1]};
	}
	public void translate(double[] t, double tdiff)
	{
		l[0]+=t[0]*tdiff/zoom;
		l[1]+=t[1]*tdiff/zoom;
	}
	public void centerCamera(double[] l)
	{
		this.l[0] = l[0]-dwidth/2;
		this.l[1] = l[1]+dheight/2;
	}
	/**
	 * gets the proper affine transofrm representative of the camera's view
	 * @return
	 */
	public AffineTransform getTransform()
	{
		AffineTransform at = new AffineTransform();
		at.translate(-l[0]*zoom, l[1]*zoom);
		at.scale(zoom, zoom);
		return at;
	}
	/**
	 * translates the passed mouse click into game space
	 * @param m the coordinates of the mouse click
	 * @return returns the game location of the mouse click
	 */
	public double[] getGameLocation(int[] m)
	{
		//System.out.println(l[0]+" + ("+m[0]+" / "+dwidth+") * ("+zoom+" * "+dwidth+") =");
		/*return new double[]{l[0]+(m[0]*1./dwidth)*(dwidth/zoom),
				-l[1]+(m[1]*1./dheight)*(dheight/zoom)};*/
		return new double[]{l[0]+(m[0]/zoom),
				-l[1]+(m[1]/zoom)}; //simplified from above line
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
