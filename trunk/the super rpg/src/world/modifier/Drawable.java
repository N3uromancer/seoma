package world.modifier;

import geom.Boundable;

import java.awt.Graphics2D;

/**
 * defines an interface for objects that can be drawn and displayed
 * @author Jack
 *
 */
public interface Drawable extends Boundable
{
	/**
	 * draws the drawable object to the screen with the passed graphics context
	 * @param g
	 */
	public void draw(Graphics2D g);
	/**
	 * checks to see if the drawble object should be displayed
	 * @return returns true if the object should be displayed, false otherwise
	 */
	public boolean isDisplayed();
}
