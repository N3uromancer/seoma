package world.effect;

import geom.Rectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;

import utilities.MathUtil;
import world.modifier.Drawable;
import world.shot.Shot;

public final class Explosion extends Effect implements Drawable
{
	private double[][] p; //particles
	private HashMap<Integer, Color> colors = new HashMap<Integer, Color>(); //color of the particles
	private Rectangle bounds = new Rectangle(0, 0, 0, 0);
	
	/**
	 * creates a new explosion
	 * @param s the shot that caused the explosion
	 * @param time
	 */
	public Explosion(Shot shot, double time)
	{
		super(time);
		
		double angleRange = 40; //how far the velocities of the particles can differ from the shot velocity
		double maxRadius = 9;
		int particles = (int)(shot.getDamage()/10+MathUtil.magnitude(shot.getVelocity())/50);
		//System.out.println("particles = "+particles);
		p = new double[particles][5]; //x, y, vx, vy, radius
		double redColorChance = .20; //chance of creating red/orange color particles, others are gray
		
		int[] grayStart = {140, 140, 140};
		int[] redStart = {220, 50, 10};
		int grayRange = 30; //range of color for grays
		int redRange = 20;
		
		for(int i = 0; i < particles; i++)
		{
			double[] s = shot.getLocation();
			double theta = Math.random()*angleRange-angleRange/2.;
			
			double[] shotVelocity = shot.getVelocity();
			shotVelocity[0]*=2.2;
			shotVelocity[1]*=2.2;
			
			double[] v = MathUtil.rotateVector(theta, shotVelocity);
			double radius = Math.random()*maxRadius;
			p[i] = new double[]{s[0], s[1], v[0]*Math.random(), v[1]*Math.random(), radius};
			
			int[] color = grayStart;
			int range = grayRange;
			if(Math.random() > redColorChance)
			{
				color = redStart;
				range = redRange;
			}
			for(int a = 0; a < color.length; a++)
			{
				color[a] += range*Math.random()-range/2;
				if(color[a] < 0)
				{
					color[a] = 0;
				}
				else if(color[a] > 255)
				{
					color[a] = 255;
				}
			}
			colors.put(i, new Color(color[0], color[1], color[2]));
		}
	}
	public void updateEffect(double tdiff)
	{
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		for(int i = 0; i < p.length; i++)
		{
			//p[i] = new double[]{p[i][0]+p[i][2]*tdiff, p[i][1]+p[i][3]*tdiff};
			p[i][0] += p[i][2]*tdiff;
			p[i][1] += p[i][3]*tdiff;
			
			p[i][3] -= .9*p[i][3]*tdiff; //decelerating the velocity
			p[i][4] -= .9*p[i][4]*tdiff;
			//System.out.println(p[i][3]);
			
			if(p[i][0]-p[i][4] < minX)
			{
				minX = p[i][0];
			}
			if(p[i][0]+p[i][4] > maxX)
			{
				maxX = p[i][0];
			}
			if(p[i][1]-p[i][4] < minY)
			{
				minY = p[i][1];
			}
			if(p[i][1]+p[i][4] > maxY)
			{
				maxY = p[i][1];
			}
		}
		bounds = new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}
	public void draw(Graphics2D g)
	{
		for(int i = 0; i < p.length; i++)
		{
			g.setColor(colors.get(i));
			g.fillOval((int)(p[i][0]-p[i][4]), (int)(p[i][1]-p[i][4]), (int)(p[i][4]*2), (int)(p[i][4]*2));
		}
	}
	public Rectangle getBounds()
	{
		return bounds;
	}
}
