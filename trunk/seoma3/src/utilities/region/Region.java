package utilities.region;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.media.opengl.GL;

/**
 * defines an abstract region of space
 * @author Jack
 *
 */
public interface Region
{
	/**
	 * determines if the passed location is contained inside this region
	 * @param px the x coordinate of the location
	 * @param py the y coordinate of the location
	 * @return returns true if the passed location is contained inside this
	 * region, false otherwise
	 */
	public boolean contains(double px, double py);
	/**
	 * checks to see if the passed region intersects or is cotained within
	 * this region, if the passed region is on the edge of this region it
	 * counts as intersecting
	 * @param r
	 * @return returns true if the passed region intersects or is contained
	 * within this region, false otherwise
	 */
	public boolean intersects(Region r);
	public void drawRegion(GL gl, double depth);
	public void fillRegion(GL gl, double depth);
	/**
	 * gets the location of the bottom left corner of the region
	 * @return returns a double[] of length 2 representing the x and y coordinates
	 * of the bottom left corner of the region
	 */
	public double[] getLocation();
	public void setLocation(double x, double y);
	public double getWidth();
	public double getHeight();
	/**
	 * writes the state of the region to the passed output stream
	 * @param dos
	 * @throws IOException
	 */
	public void writeRegion(DataOutputStream dos) throws IOException;
	/**
	 * reads and returns the state of a region from the passed input stream
	 * @param dis
	 * @return returns a region whose state was read from the passed input stream
	 * @throws IOException
	 */
	public void readRegion(DataInputStream dis) throws IOException;
}
