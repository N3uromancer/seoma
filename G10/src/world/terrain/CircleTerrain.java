package world.terrain;

import java.awt.Graphics2D;

import geom.Circle;
import utilities.MathUtil;
import world.modifier.Drawable;
import world.modifier.PathObstacle;
import world.modifier.Pathable;

/**
 * defines a circular terrain element
 * @author Secondary
 *
 */
public class CircleTerrain extends Circle implements Drawable, PathObstacle
{
	public CircleTerrain(double[] l, double radius)
	{
		super(l, radius);
	}
	public void draw(Graphics2D g)
	{
		int x = (int)(l[0]-radius);
		int y = (int)(l[1]-radius);
		g.fillOval(x, y, (int)radius*2, (int)radius*2);
	}
	public double getDistance(Pathable p)
	{
		return MathUtil.distance(l[0], l[1], p.getLocation()[0], p.getLocation()[1])-radius-p.getRadius();
	}
	public boolean intersects(Pathable p, double tdiff)
	{
		double[] pl = p.getLocation();
		pl[0]+=p.getVelocity()[0]*tdiff;
		pl[1]+=p.getVelocity()[1]*tdiff;
		
		return MathUtil.distance(l[0], l[1], pl[0], pl[1])-radius-p.getRadius() < 0;
	}
}
