package gameEngine.world.shot;

import utilities.MathUtil;
import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Unit;

/**
 * a shot that travels in the conventional manner, once shot it
 * continues along its path until it collides with something or
 * reaches its target position
 * @author Jack
 *
 */
public class StaticVelocityShot extends Shot
{
	private double[] sf = new double[2]; //the final position of the shot, declares itself dead when it reaches this position
	
	public StaticVelocityShot(double x, double y, double width, double height, Unit target, double movement, double damage, Owner o)
	{
		super(x, y, width, height, target, movement, damage, o);

		/*
		 * overshot percent, how much firther the shot travels before stopping,
		 * a percent of the distance from the starting position of the shot to
		 * the target's position
		 */
		double op = .5;
		
		double[] s = target.getLocation();
		s[0]+=target.getWidth()/2;
		s[1]+=target.getHeight()/2;
		
		double[] d = {s[0]-x, s[1]-y};
		sf[0] = s[0]+op*d[0];
		sf[1] = s[1]+op*d[1];
	}
	/**
	 * moves the shot towards its final position, sets the shot to
	 * dead if it reaches the final position
	 */
	public void updateShot(double tdiff)
	{
		double m = movement*tdiff;
		//System.out.println(m);
		//System.out.println(MathUtil.distance(x, y, sf[0], sf[1]));
		if(MathUtil.distance(x, y, sf[0], sf[1]) <= m)
		{
			dead = true;
		}
		double[] ab = {x-sf[0], y-sf[1]};
		double distance = m*m;
		double lambda = (ab[0]*ab[0])+(ab[1]*ab[1]);
		lambda = -Math.sqrt(distance / lambda);
		double[] l = {x+ab[0]*lambda, y+ab[1]*lambda};
		setLocation(l[0], l[1]);
	}
}
