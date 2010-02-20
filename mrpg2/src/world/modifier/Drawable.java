package world.modifier;

import geom.Boundable;

import java.awt.Graphics2D;

/**
 * signifies an object should be drawn to the screen
 * @author Jack
 *
 */
public interface Drawable extends Boundable
{
	/**
	 * draws the drawable object
	 * @param g
	 * @param width the width of the drawing screen
	 * @param height the height of the drawing screen
	 */
	public void draw(Graphics2D g, int width, int height);
}