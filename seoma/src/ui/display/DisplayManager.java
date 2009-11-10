package ui.display;

import javax.media.opengl.GL;

/**
 * represents a class that controls what is drawn to screen
 * @author Jack
 *
 */
public interface DisplayManager
{
	public void display(GL gl, int viewWidth, int viewHeight);
}
