package gameEngine.world.unit;

/**
 * specifies a build order, must be registered with the 
 * unit engine in order to actually make something
 * @author Jack
 *
 */
public final class BuildOrder
{
	double buildTime;
	double time;
	String name;
	Unit builder;
	
	/**
	 * creates a new build oder
	 * @param time the build time, time before the thing specified is made
	 * @param name the name of the unit to create
	 * @param the unit that initiated the build order
	 */
	public BuildOrder(double time, String name, Unit builder)
	{
		this.buildTime = time;
		this.time = time;
		this.name = name;
		this.builder = builder;
	}
	public void update(UnitEngine ue)
}
