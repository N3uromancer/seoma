package display;

import javax.media.opengl.GL;

import ui.display.DisplayManager;
import world.World;

public class Display implements DisplayManager
{
	World w;
	
	public Display(World w)
	{
		this.w = w;
	}
	public void display(GL gl, int width, int height)
	{
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, width, 0, height, -1, 1);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glClearColor(0, 1, 0, 1);
		w.drawAll(0, 0, width, height, gl);
	}
}
