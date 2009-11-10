package gameEngine.world;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.opengl.GL;

import ui.userIO.userInput.UserInput;

/**
 * represents the game world
 * @author Jack
 *
 */
public class World
{
	/**
	 * the list of updateable world objects updated in the main update loop
	 */
	LinkedList<Updateable> u = new LinkedList<Updateable>();
	
	double[] backgroundColor;
	int width; //the world width
	int height; //the world height
	
	/**
	 * creates a new world loaded from the passed file, if the
	 * file is null then a blank world is created of the default
	 * dimensions
	 * @param f the file representing the world to be loaded
	 */
	public World(File f)
	{
		if(f == null)
		{
			width = 2000;
			height = 2000;
			backgroundColor = new double[]{.1059, .7608, .0706};
		}
	}
	/**
	 * creates a new blank world
	 */
	public World()
	{
		this(null);
	}
	/**
	 * returns the list of objects representing all objects that
	 * are updated in the main update loop
	 * @return returns all updateable objects that are updated in
	 * the main update loop
	 */
	public LinkedList<Updateable> getUpdateList()
	{
		return u;
	}
	/**
	 * draws the world
	 * @param gl
	 * @param x the x coordinate of the bottom left corner of the view area
	 * @param y the y coordinate of the bottom left corner of the view area
	 * @param swidth the width of the view screen
	 * @param sheight the height of the view screen
	 */
	public void drawWorld(GL gl, int x, int y, int swidth, int sheight)
	{
		//draws the background
		gl.glColor3d(backgroundColor[0], backgroundColor[1], backgroundColor[2]);
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3d(0, 0, DepthConstants.background);
		gl.glVertex3d(swidth, 0, DepthConstants.background);
		gl.glVertex3d(swidth, sheight, DepthConstants.background);
		gl.glVertex3d(0, sheight, DepthConstants.background);
		gl.glEnd();
	}
	public void updateWorld(UserInput[] ui, double tdiff)
	{
		Iterator<Updateable> i = u.iterator();
		while(i.hasNext())
		{
			Updateable u = i.next();
			u.update(this);
			if(u.isDead())
			{
				u.destroy(this);
				i.remove();
			}
		}
	}
	/**
	 * returns the width of the world
	 * @return returns the width of the world
	 */
	public int getWidth()
	{
		return width;
	}
	/**
	 * returns the height of the world
	 * @return returns the height of the world
	 */
	public int getHeight()
	{
		return height;
	}
}
