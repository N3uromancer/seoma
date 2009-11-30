package ui.display;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 * justs displays the sasociated displayable, if no displayable is associated then
 * nothing is drawn
 * @author Jack
 *
 */
public final class Display implements GLEventListener
{
	int width; //the width of the view area
	int height;
	
	//Font font = new Font("SansSerif", Font.PLAIN, 12);
    //TextRenderer tr = new TextRenderer(font, true, false);
    
    DisplayManager displayable;
    
	public void display(GLAutoDrawable d)
	{
		GL gl = d.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);


		/*gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, this.width, 0, this.height, -1, 1);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();*/
		
		if(displayable != null)
		{
			displayable.display(gl, width, height);
		}
	}
	/**
	 * sets the displayable that the display displays
	 * @param d
	 */
	public void setDisplayable(DisplayManager d)
	{
		displayable = d;
	}
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2){}
	public void init(GLAutoDrawable d)
	{
		GL gl = d.getGL();
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glClearColor(0, 0, 0, 0);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}
	public void reshape(GLAutoDrawable d, int arg1, int arg2, int width, int height)
	{
		GL gl = d.getGL();
		gl.glViewport(0, 0, width, height);
		this.width = width;
		this.height = height;
	}
	public int getViewWidth()
	{
		return width;
	}
	public int getViewHeight()
	{
		return height;
	}
}
