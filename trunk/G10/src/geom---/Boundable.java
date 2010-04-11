package geom;

/**
 * defines an interface for things that can be bounded by some
 * rectangular bounds
 * @author Jack
 *
 */
public interface Boundable
{
	/**
	 * gets the rectangular bounds surronding this boudable object
	 * @return
	 */
	public Rectangle getBounds();
}
