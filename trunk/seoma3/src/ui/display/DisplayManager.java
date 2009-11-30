package ui.display;

import javax.media.opengl.GL;

public interface DisplayManager
{
	/**
	 * displays the class whose display is managed by the display manager
	 * @param gl
	 * @param width the screen width
	 * @param height the screen height
	 */
	public void display(GL gl, int width, int height);
}
