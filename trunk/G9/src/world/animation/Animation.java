package world.animation;

import javax.media.opengl.GL;

/**
 * defines an animation, something drawn to the screen for a time
 * @author Jack
 *
 */
public interface Animation
{
	public void update(double tdiff, AnimationEngine ae);
	public void drawAnimation(GL gl, int width, int height);
	public boolean isDead();
}
