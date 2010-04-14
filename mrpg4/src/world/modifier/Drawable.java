package world.modifier;

import geom.Boundable;

import java.awt.DisplayMode;
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
	 * @param dm
	 */
	public void draw(Graphics2D g, DisplayMode dm);
}