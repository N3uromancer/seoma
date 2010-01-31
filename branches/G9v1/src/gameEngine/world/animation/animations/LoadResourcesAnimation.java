package gameEngine.world.animation.animations;

import javax.media.opengl.GL;

import utilities.MathUtil;

import gameEngine.world.animation.Animation;
import gameEngine.world.animation.AnimationEngine;

public class LoadResourcesAnimation implements Animation
{
	double x;
	double y;
	
	double time = 0;
	boolean dead = false;
	
	public LoadResourcesAnimation(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	public void drawAnimation(GL gl, int width, int height)
	{
		int lines = 3;
		double maxLength = 20;
		double d = -.1; //depth
		gl.glPushMatrix();
		gl.glColor4d(.7, .2, .9, .5);
		gl.glTranslated(x, y, 0);
		gl.glBegin(GL.GL_LINES);
		for(int i = 0; i < lines; i++)
		{
			double length = maxLength*Math.random();
			double[] v = MathUtil.rotateVector(Math.random()*360, new double[]{length, 0});
			gl.glVertex3d(0, 0, d);
			gl.glVertex3d(v[0], v[1], d);

			//gl.glRotated(Math.random()*360, 0, 0, 1);
			//gl.glVertex3d(0, 0, d);
			//gl.glVertex3d(length, 0, d);
		}
		gl.glEnd();
		gl.glPopMatrix();
	}
	public boolean isDead()
	{
		return dead;
	}
	public void update(double tdiff, AnimationEngine ae)
	{
		time+=tdiff;
		if(time > .5)
		{
			dead = true;
		}
	}
}
