package world.modifier;

/**
 * types are sent over the network when instantiating network objects, they let
 * the receiving computer determine what type of network object to create
 * @author Jack
 *
 */
public enum ObjectType
{
	unit(Byte.MIN_VALUE),
	item((byte)(Byte.MIN_VALUE+1));
	
	private byte type;
	ObjectType(byte type)
	{
		this.type = type;
	}
	public byte getTypeCode()
	{
		return type;
	}
}
