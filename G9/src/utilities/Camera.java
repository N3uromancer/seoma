package utilities;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class Camera
{
	double[] model;
	double[] projection;
	int[] viewport;
	double x;
	double y;
	
	/**
	 * creates a new camera
	 * @param x the x coordinate of the location of bottom left
	 * portion of the screen to be viewed when the camera is instantiated
	 * @param y the y coordinate of the location of bottom left
	 * portion of the screen to be viewed when the camera is instantiated
	 */
	public Camera(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	/**
	 * translates the view location of the camera by the
	 * passed amounts
	 * @param xt
	 * @param yt
	 */
	public void translate(double xt, double yt)
	{
		x+=xt;
		y+=yt;
	}
	/**
	 * unprojects the passed point
	 * @param x
	 * @param y the y coordinate of the point to be unprojected in the
	 * opengl coordinate system
	 */
	public void unproject(int x, int y)
	{
		double[] p = new double[4];
		GLU glu = new GLU();
		glu.gluUnProject(x, y, 0, model, 0, projection, 0, viewport, 0, p, 0);
	}
	/**
	 * must be called to update the camera every time any of the following changes:
	 * model view matrix
	 * viewport
	 * projection matrix
	 * @param gl
	 */
	public void updateCamera(GL gl)
	{
		model = new double[16];
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, model, 0);
		viewport = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		projection = new double[16];
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection, 0);
	}
}
