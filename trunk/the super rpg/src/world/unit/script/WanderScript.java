package world.unit.script;

import world.World;
import world.unit.Unit;
import world.unit.attribute.Attribute;

public class WanderScript extends Script
{
	int direction = 1;
	
	public void updateScript(Unit u, World w, double tdiff)
	{
		double[] l = u.getLocation();
		double m = u.getAttributeManager().getAttribute(Attribute.movement);
		//System.out.println(m);
		//System.out.println(u.getID()+" updated, ("+(int)l[0]+", "+(int)l[1]+")");
		if(l[0] > 500)
		{
			direction = -1;
			//setDead();
		}
		if(l[0] < 0)
		{
			direction = 1;
		}
		l[0]+=m*tdiff*direction;
		u.setLocation(l);
		//intersects = w.getAssociatedRegion(getID()).getIntersectedUnits(this).size() > 0;
	}
}
