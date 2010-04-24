package world.unit;

public class StillUnit extends Unit
{
	public StillUnit(double[] l)
	{
		super(l);
	}
	public int getUpdatePriority()
	{
		return 5;
	}
	public void update(double tdiff)
	{
		double[] l = getLocation();
		double m = 50;
		setLocation(new double[]{l[0]+m*tdiff, l[1]});
	}
}
