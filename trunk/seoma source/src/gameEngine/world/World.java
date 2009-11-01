package gameEngine.world;

import java.io.File;

import javax.media.opengl.GL;

import ui.userIO.userInput.UserInput;
import utilities.SpatialPartition;


public class World
{
	SpatialPartition p; //stores the world polygons
	
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
		double d = -1; //depth
		gl.glColor3d(backgroundColor[0], backgroundColor[1], backgroundColor[2]);
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3d(0, 0, d);
		gl.glVertex3d(swidth, 0, d);
		gl.glVertex3d(swidth, sheight, d);
		gl.glVertex3d(0, sheight, d);
		gl.glEnd();
	}
	public void updateWorld(UserInput[] ui, double tdiff)
	{
		
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
