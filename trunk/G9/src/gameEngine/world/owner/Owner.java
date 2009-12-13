package gameEngine.world.owner;

/**
 * stores the owner dependent stats such as resources and unit count
 * @author Jack
 *
 */
public class Owner
{
	String name;
	double resources = 0;
	int unitCount = 0;
	double[] c; //owner color
	byte id;
	
	public Owner(byte id, String name, double[] c)
	{
		this.id = id;
		this.name = name;
		this.c = c;
	}
	public byte getID()
	{
		return id;
	}
	public double[] getColor()
	{
		return c;
	}
}
