package world.unit;

/**
 * the player controlled unit
 * @author Jack
 *
 */
public class Avatar extends Unit
{
	byte region;
	
	public Avatar(double[] l)
	{
		super(l);
	}
	public void update(double tdiff)
	{
		
	}
	/**
	 * gets the id of the region the avatar resides in
	 * @return returns the id of the region the avatar resides in
	 */
	public byte getRegion()
	{
		return region;
	}
	/**
	 * sets the region the avatar currently resides in
	 * @param id
	 */
	public void setRegion(byte id)
	{
		region = id;
	}
}
