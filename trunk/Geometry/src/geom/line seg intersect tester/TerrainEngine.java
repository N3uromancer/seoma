package world.terrain;

import geom.Boundable;
import geomUtil.PartitionManager;

import java.util.HashSet;

/**
 * stores all terrain and determines if things intersect the terrain
 * @author Secondary
 *
 */
public class TerrainEngine
{
	PartitionManager pm;
	
	public TerrainEngine(double width, double height)
	{
		pm = new PartitionManager(0, 0, width, height, 30, 50, 100);
	}
	public void addLineSegment(LineSegment ls)
	{
		pm.add(ls);
	}
	/**
	 * determines if the passed point p moving with velocity v intersects
	 * any of the terrain features
	 * @param p
	 * @param v
	 * @param tdiff
	 * @return
	 */
	public boolean intersects(double[] p, double[] v, double tdiff)
	{
		double xmin = p[0] < p[0]+v[0]*tdiff ? p[0] : p[0]+v[0]*tdiff;
		double ymin = p[1] < p[1]+v[1]*tdiff ? p[1] : p[1]+v[1]*tdiff;
		HashSet<Boundable> bset = pm.intersects(xmin, ymin, Math.abs(v[0]*tdiff), Math.abs(v[1]*tdiff));
		for(Boundable b: bset)
		{
			LineSegment ls = (LineSegment)b;
			double dist1 = ls.distance(p);
			double dist2 = ls.distance(new double[]{p[0]+v[0]*tdiff, p[1]+v[1]*tdiff});
			if((dist1 > 0 && dist2 < 0) || (dist1 < 0 && dist2 > 0) || dist1 == 0 || dist2 == 0)
			{
				//crossed the line or on it
				return true;
			}
		}
		return false;
	}
}
