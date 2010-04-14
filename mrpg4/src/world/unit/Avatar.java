package world.unit;

/**
 * the player controlled unit
 * @author Jack
 *
 */
public class Avatar extends Unit
{
	public Avatar(double[] l)
	{
		super(l);
	}
	public void update(double tdiff)
	{
		
	}
	public int getUpdatePriority()
	{
		return 10;
	}
}
