package unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.media.opengl.GL;
import ui.userIO.userInput.UserInput;
import updateManager.Drawable;
import updateManager.UpdateManager;
import updateManager.Updateable;
import updateManager.Updater;
import utilities.SpatialPartition;
import utilities.region.Region;

public class UnitEngine implements Updater
{
	SpatialPartition u;
	HashMap<Byte, LinkedList<Unit>> units = new HashMap<Byte, LinkedList<Unit>>();
	
	public UnitEngine(int width, int height)
	{
		u = new SpatialPartition(0, 0, width, height, 30, 100, 50);
	}
	public void drawAll(double x, double y, double width, double height, GL gl)
	{
		Iterator<Region> i = u.getIntersections(x, y, width, height).iterator();
		while(i.hasNext())
		{
			Unit u = (Unit)i.next();
			u.draw(gl);
		}
	}
	public void register(Updateable u)
	{
		System.out.println("updateable unit registered");
		Unit unit = (Unit)u;
		if(units.get(unit.getOwner()) != null)
		{
			units.get(unit.getOwner()).add(unit);
		}
		else
		{
			LinkedList<Unit> temp = new LinkedList<Unit>();
			temp.add(unit);
			units.put(unit.getOwner(), temp);
		}
		this.u.addRegion((Region)u);
	}
	public void register(Drawable d)
	{
		System.out.println("drawable unit registered");
	}
	public void update(UpdateManager um, double tdiff, HashMap<Byte, ArrayList<UserInput>> ui)
	{
		Iterator<Byte> i = units.keySet().iterator();
		while(i.hasNext())
		{
			Iterator<Unit> i2 = units.get(i.next()).iterator();
			while(i2.hasNext())
			{
				Unit unit = i2.next();
				u.removeRegion(unit);
				unit.update(um, tdiff, ui);
				u.addRegion(unit);
			}
		}
	}
	public boolean isDead()
	{
		return false;
	}
}
