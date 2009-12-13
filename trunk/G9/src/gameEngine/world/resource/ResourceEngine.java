package gameEngine.world.resource;

import gameEngine.world.World;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;

public class ResourceEngine
{
	ArrayList<ResourceDeposit> r = new ArrayList<ResourceDeposit>();
	
	public ResourceEngine(ArrayList<ResourceDeposit> r)
	{
		this.r = r;
	}
	public void  updateResourceDeposits(double tdiff, World w)
	{
		Iterator<ResourceDeposit> i = r.iterator();
		while(i.hasNext())
		{
			i.next().update(w, tdiff);
		}
	}
	public void drawResourceDeposits(GL gl)
	{
		Iterator<ResourceDeposit> i = r.iterator();
		while(i.hasNext())
		{
			i.next().draw(gl, 1);
		}
	}
}
