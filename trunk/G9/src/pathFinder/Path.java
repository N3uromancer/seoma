package pathFinder;

import utilities.MathUtil;
import gameEngine.world.unit.Unit;

public class Path
{
	Node[] n;
	int index = 0; //the node the unit is currently traveling to
	
	double[] target; //the target location of the path
	
	/**
	 * creates a path to follow
	 * @param n the list of nodes to move across
	 * @param x the x coordinate of the final location
	 * @param y the y coordinate of the final location
	 */
	public Path(Node[] n, double x, double y)
	{
		this.n = n;
		target = new double[]{x, y};
	}
	public void moveUnit(Unit u)
	{
		double tx = n[index].getCenter();
		
		double[] s = u.getLocation();
		double movement = u.getMovement()*tdiff;
		if(MathUtil.distance(s[0], s[1], tx, ty) <= movement)
		{
			u.setLocation(tx, ty);
		}
		double[] ab = {s[0]-tx, s[1]-ty};
		double distance = movement*movement;
		double lambda = (ab[0]*ab[0])+(ab[1]*ab[1]);
		
		lambda = -Math.sqrt(distance / lambda);
		//lambda = distance / lambda;
		
		double[] l = {s[0]+ab[0]*lambda, s[1]+ab[1]*lambda};
		u.setLocation(l[0], l[1]);
	}
}
