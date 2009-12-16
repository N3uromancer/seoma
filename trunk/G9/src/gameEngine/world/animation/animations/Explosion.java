package gameEngine.world.animation.animations;

import javax.media.opengl.GL;

import utilities.MathUtil;

import gameEngine.world.animation.Animation;
import gameEngine.world.animation.AnimationEngine;
import gameEngine.world.unit.Unit;

public class Explosion implements Animation
{
	Triangle[] ut; //unit colored triangles
	Triangle[] et; //explosion triangles
	double[] c;
	boolean dead = false;
	double time;
	double maxTime;
	
	/**
	 * creates a new explosion
	 * @param u the unit that died and caused the explosion
	 */
	public Explosion(Unit u)
	{
		int unitTriangles = 2; //number of triangles of the same color as the dying unit
		c = u.getOwner().getColor();
		maxTime = .4;
		
		ut = createTriangles(u, unitTriangles);
		et = createTriangles(u, unitTriangles*2);
	}
	private Triangle[] createTriangles(Unit u, int number)
	{
		Triangle[] t = new Triangle[number];
		for(int i = 0; i < t.length; i++)
		{
			double[] p1 = {u.getLocation()[0]+u.getWidth()/2, u.getLocation()[1]+u.getHeight()/2};
			double[] v1 = {Math.random()*u.getWidth(), u.getHeight()/2};
			double[] v2 = {u.getWidth()/2, Math.random()*u.getHeight()};
			double[] velocity = MathUtil.rotateVector(Math.random()*360, new double[]{Math.random()*u.getWidth()*10, 0});
			
			t[i] = new Triangle();
			t[i].setVectors(p1, v1, v2, velocity);
		}
		return t;
	}
	public void drawAnimation(GL gl, int width, int height)
	{
		gl.glColor3d(c[0], c[1], c[2]);
		gl.glBegin(GL.GL_TRIANGLES);
		for(int i = 0; i <ut.length; i++)
		{
			ut[i].drawTriangle(gl);
		}
		gl.glEnd();
		gl.glColor3d(.9, .3, .1);
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
