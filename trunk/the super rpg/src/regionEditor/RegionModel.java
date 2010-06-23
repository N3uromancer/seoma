package regionEditor;

import geom.Rectangle;

import java.awt.Color;
import java.awt.Graphics2D;

import world.World;
import world.terrain.Terrain;
import display.Camera;

/**
 * defines a model for a region that can be modified by the editor
 * @author Jack
 *
 */
public class RegionModel
{
	Color background = Color.green;
	int[] dim;
	Terrain[][] t;
	
	public RegionModel()
	{
		setSize(1000, 1000);
	}
	public void setBackgroundColor(Color c)
	{
		background = c;
	}
	public Color getBackground()
	{
		return background;
	}
	public void drawRegionModel(Graphics2D g, Camera c, int width, int height)
	{
		double[] l = c.getLocation();
		l[1]*=-1;
		Rectangle r = c.getViewBounds();
		//int draws = 0;
		for(int x = (int)(l[0]/World.gridSize) < 0? 0: (int)(l[0]/World.gridSize); x < t.length && x <= (int)((l[0]+r.getWidth())/World.gridSize); x++)
		{
			for(int y = (int)(l[1]/World.gridSize) < 0? 0: (int)(l[1]/World.gridSize); y < t[0].length && y <= (int)((l[1]+r.getHeight())/World.gridSize)+1; y++)
			{
				//draws++;
				g.setColor(t[x][y].getColor());
				g.fillRect(x*World.gridSize, y*World.gridSize, World.gridSize, World.gridSize);
			}
		}
		//System.out.println(draws);
		
		g.setColor(Color.black);
		g.drawRect(0, 0, dim[0], dim[1]);
		
		

		g.setColor(Color.black);
		for(int i = 0; i <= dim[0]; i+=World.gridSize)
		{
			g.drawLine(i, 0, i, dim[1]);
		}
		for(int i = 0; i <= dim[1]; i+=World.gridSize)
		{
			g.drawLine(0, i, dim[0], i);
		}
	}
	public void setTerrain(double[] l, Terrain t)
	{
		try
		{
			this.t[(int)l[0]/World.gridSize][(int)l[1]/World.gridSize] = t;
		}
		catch(ArrayIndexOutOfBoundsException e){}
	}
	/**
	 * sets the size of the region
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height)
	{
		dim = new int[2];
		dim[0] = width/World.gridSize*World.gridSize;
		dim[1] = height/World.gridSize*World.gridSize;
		
		Terrain[][] temp = new Terrain[width/World.gridSize][height/World.gridSize];
		for(int x = 0; x < temp.length; x++)
		{
			for(int y = 0; y < temp[0].length; y++)
			{
				temp[x][y] = Terrain.grass;
			}
		}
		if(t != null)
		{
			//transfers over already placed terrain
			for(int x = 0; x < t.length; x++)
			{
				for(int y = 0; y < t[0].length; y++)
				{
					try
					{
						temp[x][y] = t[x][y];
					}
					catch(ArrayIndexOutOfBoundsException e){}
				}
			}
		}
		t = temp;
	}
	public int[] getSize()
	{
		return dim;
	}
}
