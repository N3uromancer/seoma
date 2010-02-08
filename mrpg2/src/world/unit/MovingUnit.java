package world.unit;

/**
 * a unit that moves randomly
 * @author Jack
 *
 */
public class MovingUnit extends Unit
{
	int x;
	int y;
	int width;
	int height;
	
	/**
	 * creates a new unit that moves randomly when updated
	 * @param l
	 * @param x the x coordinate of the  bottom left corner of the region
	 * in which the unit is randomly moved
	 * @param y the y coordinate of the  bottom left corner of the region
	 * in which the unit is randomly moved
	 * @param width the width of the region the unit is to move within
	 * @param height the height of the region the unit is to move within
	 */
	public MovingUnit(double[] l, int x, int y, int width, int height)
	{
		super(l);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public void update(double tdiff)
	{
		double movement = 10;
		l[0]+=Math.random()*movement-movement/2;
		if(l[0] < x)
		{
			l[0] = x;
		}
		else if(l[0] > x+width)
		{
			l[0] = x+width;
		}
		l[1]+=Math.random()*movement-movement/2;
		if(l[1] < y)
		{
			l[1] = y;
		}
		else if(l[1] > y+height)
		{
			l[1] = y+height;
		}
	}
}
