package pathFinder;

public interface Node
{
	/**
	 * checks to see if the passed point is contained inside the
	 * node meaning the unit must move to the next node
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean contains(double x, double y);
	public double[] getCenter();
}
