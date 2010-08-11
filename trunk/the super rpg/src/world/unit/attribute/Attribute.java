package world.unit.attribute;

public enum Attribute
{
	health("health"),
	movement("movement");
	
	private String s;
	Attribute(String s)
	{
		this.s = s;
	}
	public String toString()
	{
		return s;
	}
}
