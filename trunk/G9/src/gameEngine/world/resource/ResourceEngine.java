package gameEngine.world.resource;


import gameEngine.world.World;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;


public class ResourceEngine
{
	ArrayList<ResourceDeposit> rd = new ArrayList<ResourceDeposit>();
	
	public ResourceEngine(ArrayList<ResourceDeposit> r)
	{
		this.rd = r;
	}
	public void  updateResourceEngine(double tdiff, World w)
	{
		Iterator<ResourceDeposit> i = rd.iterator();
		while(i.hasNext())
		{
			i.next().update(w, tdiff);
		}
	}
	public ArrayList<ResourceDeposit> getResourceDeposits()
	{
		return rd;
	}
	public void drawResourceDeposits(GL gl)
	{
		Iterator<ResourceDeposit> i = rd.iterator();
		while(i.hasNext())
		{
			i.next().draw(gl, 1);
		}
	}
}
