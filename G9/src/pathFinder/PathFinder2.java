package pathFinder;

import javax.media.opengl.GL;

import utilities.Location;
import utilities.Polygon;

public abstract class PathFinder2
{
	protected int width;
	protected int height;
	protected Polygon[] p;
	
	long pathingTime = 0;
	long pathsFound = 0;
	
	public PathFinder2(int width, int height, Polygon[] p)
	{
		this.width = width;
		this.height = height;
		this.p = p;
	}
	/**
	 * draws the pathing world
	 * @param gl
	 * @param drawAdjacencies if true then the adjcencies between the nodes are drawn
	 */
	public abstract void drawPathing(GL gl, boolean drawAdjacencies);
	/**
	 * gets a path from the starting location to the end location, this method is
	 * not called by classes outside of this one in order to accurately record
	 * time spent on pathfinding
	 * @param sx the x coordinate of the starting position
	 * @param sy the y coordinate of the starting position
	 * @param tx the x coordinate of the ending position
	 * @param ty the y coordinate of the ending position
	 * @return returns an array of locations representing the path
	 * to be followed
	 */
	protected abstract Location[] getPath(double sx, double sy, double tx, double ty);
	/**
	 * determines a viable path from the starting point to the ending point, records
	 * the time spent determining the path and the number of paths found
	 * @param sx the x coordinate of the starting position
	 * @param sy the y coordinate of the starting position
	 * @param tx the x coordinate of the ending position
	 * @param ty the y coordinate of the ending position
	 * @return returns an array of locations representing the path
	 * to be followed
	 */
	public Location[] determinePath(double sx, double sy, double tx, double ty)
	{
		long start = System.currentTimeMillis();
		Location[] l = getPath(sx, sy, tx, ty);
		pathingTime+=System.currentTimeMillis()-start;
		pathsFound++;
		
		if(pathsFound % 10000 == 0)
		{
			System.out.println("paths found = "+pathsFound+", total time (ms) = "+
					pathingTime+", paths per ms = "+(pathsFound*1.0/pathingTime));
		}
		
		return l;
	}
	/**
	 * returns the total time spent pathfinding
	 * @return returns the number of miliseconds spent on pathfinding
	 */
	public long getPathingTime()
	{
		return pathingTime;
	}
	/**
	 * returns the number of paths found by this pathfinder
	 * @return returns the number of paths found
	 */
	public long getPathsFound()
	{
		return pathsFound;
	}
}
