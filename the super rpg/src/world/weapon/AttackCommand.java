package world.weapon;

/**
 * represents an attack command for a particular unit
 * @author Jack
 *
 */
public final class AttackCommand
{
	private short id;
	private byte direction;
	
	/**
	 * creates a new attack command
	 * @param id the id of the attacking unit
	 * @param direction the direction of the attack
	 */
	public AttackCommand(short id, byte direction)
	{
		this.id = id;
		this.direction = direction;
	}
	public short getID()
	{
		return id;
	}
	public byte getDirection()
	{
		return direction;
	}
}
