package world;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;

/**
 * definse the entirety of the game world, the world is divided into regions
 * @author Jack
 *
 */
public class World
{
	short id = Short.MIN_VALUE;
	
	public void drawWorld(Graphics2D g, DisplayMode dm)
	{
		g.setColor(Color.green);
		g.fillRect(0, 0, dm.getWidth(), dm.getHeight());
	}
	public void updateWorld(double tdiff)
	{
		
	}
	/**
	 * generates an unused id for a game object
	 * @return
	 */
	public short generateNewID()
	{
		id++;
		return (short)(id-1);
	}
}
