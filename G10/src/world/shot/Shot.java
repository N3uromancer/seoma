package world.shot;

public class Shot
{
	double[] s;
	double[] v;
	
	double damage;
	
	public Shot(double[] s, double[] v, double damage)
	{
		this.s = s;
		this.v = v;
		this.damage = damage;
	}
	public double[] getVelocity()
	{
		return v;
	}
	public double[] getLocation()
	{
		return s;
	}
	public double getDamage()
	{
		return damage;
	}
}
