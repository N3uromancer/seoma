package modifier;

import java.awt.Graphics2D;

/**
 * signifies an object should be drawn to the screen
 * @author Jack
 *
 */
public interface Drawable
{
	public void draw(Graphics2D g, int width, int height);
}