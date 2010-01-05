package gameEngine.world.owner;

/**
 * stores the owner dependent stats such as resources and unit count
 * @author Jack
 *
 */
public class Owner
{
	String name;
	double resources = 500;
	double[] c; //owner color
	byte id;
	
	public Owner(byte id, String name, double[] c)
	{
		this.id = id;
		this.name = name;
		this.c = c;
	}
	public String getName()
	{
		return name;
	}
	public double getResources()
	{
		return resources;
	}
	public void setResources(double setter)
	{
		resources = setter;
		//System.out.println("resources = "+resources);
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
