package gameEngine.world.unit;

import gameEngine.world.owner.Owner;

import java.lang.reflect.Constructor;

/**
 * specifies a build order, must be registered with the 
 * unit engine in order to actually make something
 * @author Jack
 *
 */
public final class BuildOrder
{
	double buildTime;
	double time = 0; //the time elapsed since issue of the order
	String name;
	Unit builder;
	Class<? extends Unit> u;
	
	/**
	 * creates a new build oder
	 * @param buildTime the build time, time before the thing specified is made
	 * @param name the name of the unit to create
	 * @param the unit that initiated the build order
	 */
	public BuildOrder(double buildTime, String name, Unit builder)
	{
		this.buildTime = buildTime;
		this.name = name;
		this.builder = builder;
	}
	/**
	 * creates a new build oder
	 * @param buildTime the build time, time before the thing specified is made
	 * @param u the unit to create
	 * @param the unit that initiated the build order
	 */
	public BuildOrder(double buildTime, Class<? extends Unit> u, Unit builder)
	{
		this.buildTime = buildTime;
		this.u = u;
		this.builder = builder;
	}
	/**
	 * updates the build order, increments the timer
	 * @param tdiff
	 * @param ue
	 * @return returns true when the order is carried out and the
	 * unit is created, false otherwise
	 */
	public boolean update(double tdiff, UnitEngine ue)
	{
		time+=tdiff;
		if(time >= buildTime)
		{
			Class<?>[] arguments = {Owner.class, double.class, double.class};
			try
			{
				Object[] parameters = {builder.getOwner(), builder.getLocation()[0], builder.getLocation()[1]};

				/*Constructor<?>[] cons = u.getConstructors();
				for(int i = 0; i < cons.length; i++)
				{
					System.out.println("constructor "+(i+1)+" parameters:");
					Class<?>[] params = cons[i].getParameterTypes();
					for(int a = 0; a < params.length; a++)
					{
						System.out.println(params[a].getName()+", ");
					}
				}*/
				
				Constructor<? extends Unit> c = u.getConstructor(arguments);
				//Constructor<Unit> c = Unit.class.getConstructor(arguments);
				Unit u = c.newInstance(parameters);
				ue.registerUnit(u);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			
			//ue.registerUnit(UnitFactory.createUnit(name, builder.getOwner(), 
			//		builder.getLocation()[0], builder.getLocation()[1]));
			return true;
		}
		return false;
	}
}
