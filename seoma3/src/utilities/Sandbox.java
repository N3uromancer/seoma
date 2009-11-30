package utilities;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 * a simple drawing test class, automatically creates a frame and displays
 * everything, simply extend and fill in the draw method to use it
 * @author Jack
 *
 */
public abstract class Sandbox implements GLEventListener
{
	int width; //the width of the view area
	int height;
	
	GLFrame glf;
	
	public Sandbox()
	{
		glf = new GLFrame(this);
	}
	public Sandbox(int width, int height)
	{
		glf = new GLFrame(this, width, height);
	}
	/**
	 * returns the frame this display is attached to
	 * @return returns the frame this display is attached to
	 */
	public GLFrame getGLFrame()
	{
		return glf;
	}
	public void display(GLAutoDrawable d)
	{
		GL gl = d.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);


		/*gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, this.width, 0, this.height, -1, 1);*/
		
		draw(gl, width, height);
	}
	/**
	 * called every time the display method for the canvas is called
	 * @param gl
	 * @param width the width of the displayed area
	 * @param height the height of the displayed area
	 */
	public abstract void draw(GL gl, int width, int height);
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
}
