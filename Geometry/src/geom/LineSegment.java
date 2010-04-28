package geom;

import geom.Boundable;
import geom.Rectangle;

/**
 * defines a line segment
 * @author Secondary
 *
 */
public class LineSegment implements Boundable
{
	private double[] p1;
	private double[] p2;
	private Rectangle bounds;

	private double[] v; //a vector representing the line segment
	
	public LineSegment(double[] p1, double[] p2)
	{
		this.p1 = p1;
		this.p2 = p2;
		
		v = new double[]{p2[0]-p1[0], p2[1]-p1[1]};
		
		double xmin = p1[0] < p2[0] ? p1[0] : p2[0];
		double ymin = p1[1] < p2[1] ? p1[1] : p2[1];
		bounds = new Rectangle(xmin, ymin, Math.abs(p1[0]-p2[0]), Math.abs(p1[1]-p2[1]));
	}
	/**
	 * determines the minimum distance from the line segment to
	 * the passed point
	 * @param p
	 * @return returns the minimum distance to the line segment
	 */
	public double distance(double[] p)
	{
		double[] n = normal(p1[0], p1[1], p2[0], p2[1]); //unit vector, normalized
		//System.out.println("normal = "+n[0]+", "+n[1]);

		//lambda (l) is the distance from the line, not the segment
		double l = ((p[1]-p1[1])*v[0] - (p[0]-p1[0])*v[1]) / (n[1]*v[0] - n[0]*v[1]);
		//System.out.println("lambda = "+l);
		boolean pos = l > 0;
		
		//t is distance along V, how far along V where the normal vector is projected to the point P
		double t = v[0] != 0 ? (p[0]-p1[0]-l*n[0]) / v[0]: (p[1]-p1[1]-l*n[1]) / v[1];
		//System.out.println("t="+t);
		
		if(t >= 0 && t <= 1)
		{
			//point p lies closest to the line segment
			//System.out.println("p is closest to line segment, returning lambda");
			return l;
		}
		//System.out.println("p is closest to segment end point");
		//point p lies closer to the an end of the line segment
		double dist = t < 0 ? distance(p1[0], p1[1], p[0], p[1]) : distance(p2[0], p2[1], p[0], p[1]);
		if(!pos)
		{
			dist*=-1;
		}
		return dist;
	}
	public Rectangle getBounds()
	{
		return bounds;
	}
	private static double[] normal(double x1, double y1, double x2, double y2)
	{
		double[] v1 = {x2-x1, y2-y1, 0};
		double[] n = {-v1[1], v1[0]};
		double length = Math.sqrt(Math.pow(n[0], 2)+Math.pow(n[1], 2));
		n[0]/=length;
		n[1]/=length;
		return n;
	}
	private static double distance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
	}
}
