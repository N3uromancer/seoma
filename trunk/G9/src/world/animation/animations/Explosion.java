package world.animation.animations;

import javax.media.opengl.GL;

import utilities.MathUtil;
import world.animation.Animation;
import world.animation.AnimationEngine;

public class Explosion implements Animation
{
	Triangle[] ut; //unit colored triangles
	Triangle[] et; //explosion triangles
	double[] c;
	boolean dead = false;
	double time;
	double maxTime;
	
	double x;
	double y;
	double maxWidth;
	double maxHeight;

	/**
	 * creates a new explosion
	 * @param x
	 * @param y
	 * @param maxWidth the max width of the explosion triangles
	 * @param maxHeight the max height of the explosion triangles
	 * @param ownerColor the owner's color of the exploding unit
	 * @param unitTriangles number of triangles of the unit's owner color that are created
	 * @param explosionTriangles number of orange triangles created
	 */
	public Explosion(double x, double y, double maxWidth, double maxHeight, double[] ownerColor, int unitTriangles, int explosionTriangles)
	{
		this.x = x;
		this.y = y;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		
		c = ownerColor;
		maxTime = .4;
		
		ut = createTriangles(unitTriangles);
		et = createTriangles(explosionTriangles);
	}
	private Triangle[] createTriangles(int number)
	{
		Triangle[] t = new Triangle[number];
		for(int i = 0; i < t.length; i++)
		{
			double[] p1 = {x+maxWidth/2, y+maxHeight/2};
			double[] v1 = {Math.random()*maxWidth, maxHeight/2};
			double[] v2 = {maxWidth/2, Math.random()*maxHeight};
			double[] velocity = MathUtil.rotateVector(Math.random()*360, new double[]{Math.random()*maxWidth*10, 0});
			
			t[i] = new Triangle();
			t[i].setVectors(p1, v1, v2, velocity);
		}
		return t;
	}
	public void drawAnimation(GL gl, int width, int height)
	{
		if(c != null)
		{
			gl.glColor4d(c[0], c[1], c[2], 1);
			gl.glBegin(GL.GL_TRIANGLES);
			for(int i = 0; i <ut.length; i++)
			{
				ut[i].drawTriangle(gl);
			}
			gl.glEnd();
		}
		gl.glColor4d(.9, .3, .1, 1);
		gl.glBegin(GL.GL_TRIANGLES);
		for(int i = 0; i <et.length; i++)
		{
			et[i].drawTriangle(gl);
		}
		gl.glEnd();
	}
	public boolean isDead()
	{
		return dead;
	}
	public void update(double tdiff, AnimationEngine ae)
	{
		//System.out.println(time);
		for(int i = 0; i <ut.length; i++)
		{
			ut[i].update(tdiff);
		}
		for(int i = 0; i <et.length; i++)
		{
			et[i].update(tdiff);
		}
		time+=tdiff;
		if(time >= maxTime)
		{
			dead = true;
		}
	}
	class Triangle
	{
		double[] p1;
		double[] v1;
		double[] v2;
		double[] velocity;
		
		public void setVectors(double[] p1, double[] v1, double[] v2, double[] velocity)
		{
			this.p1 = p1;
			this.v1 = v1;
			this.v2 = v2;
			this.velocity = velocity;
		}
		public void drawTriangle(GL gl)
		{
			double d = 1; //depth
			gl.glVertex3d(p1[0], p1[1], d);
			gl.glVertex3d(p1[0]+v1[0], p1[1]+v1[1], d);
			gl.glVertex3d(p1[0]+v2[0], p1[1]+v2[1], d);
		}
		public void update(double tdiff)
		{
			p1[0]+=velocity[0]*tdiff;
			p1[1]+=velocity[1]*tdiff;
			
			velocity[0]*=.7;
			velocity[1]*=.7;
		}
	}
}
