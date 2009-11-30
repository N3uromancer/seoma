package updateManager;


import javax.media.opengl.GL;

/**
 * represents an updater, updateable and drawable things are added
 * to it and updated when the updater itself is updated, although it
 * can be declared dead and subsequently removed it is typically more
 * permanent then other updateable classes
 * @author Jack
 *
 */
public interface Updater extends Updateable
{
	public void register(Updateable u);
	public void register(Drawable d);
	/**
	 * draws all drawable objects associated with the updater
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param gl
	 */
	public void drawAll(double x, double y, double width, double height, GL gl);
}
