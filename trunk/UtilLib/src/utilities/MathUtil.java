package utilities;

/**
 * performs various mathematical operations, all vector operations are in 2D
 * @author Jack
 *
 */
public final class MathUtil
{
	public static double distance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
	}
	/**
	 * rotates a vector
	 * @param angle the angle the vector is to be rotated to, angle
	 * must be in degrees
	 * @param v the vector
	 * @return returns the newly rotated vector
	 */
	public static double[] rotateVector(double angle, double[] v)
	{
		double[] n = new double[2];
		n[0] = Math.cos(Math.toRadians(angle))*v[0]-Math.sin(Math.toRadians(angle))*v[1];
		n[1] = Math.sin(Math.toRadians(angle))*v[0]+Math.cos(Math.toRadians(angle))*v[1];
		return n;
	}
	/**
	 * determines the midpoint of the two passed points
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return returns the midpoint of the two passed points
	 */
	public static double[] midpoint(double x1, double y1, double x2, double y2)
	{
		double[] s = {(x2+x1)*.5, (y2+y1)*.5};
		return s;
	}
	/**
	 * determines the normal vector for the two passed points
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return returns the normal vector of the plane defined by the two passed points,
	 * the returned vector is normalized (has a length of 1)
	 */
	public static double[] normal(double x1, double y1, double x2, double y2)
	{
		/*Location p1 = new Location(x1, y1);
		Location p2 = new Location(x2, y2);
		Location p3 = new Location(x1, y1, -1);
		Location v1 = new Location(p2.x-p1.x, p2.y-p1.y, p2.z-p1.z); //vector 1
		Location v2 = new Location(p3.x-p1.x, p3.y-p1.y, p3.z-p1.z);
		Location normal = new Location(v1.y*v2.z-v2.y*v1.z, v2.x*v1.z-v1.x*v2.z, v1.x*v2.y-v2.x*v1.y);
		double length = Math.sqrt(Math.pow(normal.x, 2)+Math.pow(normal.y, 2)+Math.pow(normal.z, 2));
		normal = new Location(normal.x/length, normal.y/length, normal.z/length);
		double[] n = {normal.x, normal.y};
		return n;*/
		
		double[] v1 = {x2-x1, y2-y1, 0};
		//double[] v2 = {0, 0, -1};
		//double[] n = {v1[1]*v2[2]-v2[1]*v1[2], v2[0]*v1[2]-v1[0]*v2[2], v1[0]*v2[1]-v2[0]*v1[1]};
		double[] n = {-v1[1], v1[0]};
		double length = Math.sqrt(Math.pow(n[0], 2)+Math.pow(n[1], 2));
		n[0]/=length;
		n[1]/=length;
		return n;
	}
}
