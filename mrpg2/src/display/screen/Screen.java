package display.screen;

import java.awt.DisplayMode;
import java.awt.Graphics2D;

import javax.swing.JComponent;

/**
 * represents the screen being viewed by the user
 * @author Jack
 *
 */
public interface Screen
{
	public JComponent[] getComponents();
	/**
	 * called every time display method called in Display, components
	 * added to the frame are automatically drawn and should not be
	 * redrawn in this method
	 * @param g
	 * @param dm the display mode of the area that the screen is being
	 * drawn to
	 */
	public void displayScreen(Graphics2D g, DisplayMode dm);
}
