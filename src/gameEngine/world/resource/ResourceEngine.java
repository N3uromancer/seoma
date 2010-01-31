package gameEngine.world.resource;


import gameEngine.world.World;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;


public class ResourceEngine
{
	ArrayList<ResourceDeposit> rd = new ArrayList<ResourceDeposit>();
	
	long updateTime = 0;
	long updates = 0;
	
	public ResourceEngine(ArrayList<ResourceDeposit> r)
	{
		this.rd = r;
	}
	public void  updateResourceEngine(double tdiff, World w)
	{
		long start = System.currentTimeMillis();
		Iterator<ResourceDeposit> i = rd.iterator();
		while(i.hasNext())
		{
			i.next().update(w, tdiff);
		}
		updateTime+=System.currentTimeMillis()-start;
		updates++;
		if(updates%3000 == 0)
		{
			System.out.println("resource engine time per update = "+updateTime+" [total time] / "+updates+" [updates] = "+(updateTime*1./updates));
			System.out.println("-----------------------");
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
