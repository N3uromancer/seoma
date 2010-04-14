package world;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import world.modifier.GameObject;

/**
 * represents a piece of the game world
 * @author Jack
 *
 */
public class Region
{
	byte regionID;
	Semaphore s = new Semaphore(1, true); //controls access to the environement
	HashMap<Integer, GameObject> obj = new HashMap<Integer, GameObject>();
	short id = 0;
	int width;
	int height;
	
	public Region()
	{
		regionID = 3;
		width = 1000;
		height = 700;
	}
	/**
	 * creates a new id that can be used for an object in this environment
	 * @return
	 */
	public short getNewID()
	{
		return id++;
	}
	public void updateRegion(double tdiff)
	{
		
	}
	public void drawRegion(Graphics2D g, DisplayMode dm)
	{
		g.setColor(Color.green);
		g.fillRect(0, 0, dm.getWidth(), dm.getHeight());
		g.setColor(Color.black);
		g.drawRect(0, 0, width, height);
	}
	public Semaphore getSemaphore()
	{
		return s;
	}
	public byte getID()
	{
		return regionID;
	}
	/**
	 * gets the game object referenced by the passed id
	 * @param id
	 * @return return a game object
	 */
	public GameObject getObject(int id)
	{
		return obj.get(id);
	}
}
