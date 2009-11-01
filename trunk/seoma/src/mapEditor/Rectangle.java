package mapEditor;

import javax.media.opengl.GL;

import utilities.MathUtil;

public class Rectangle
{
	double[][] p = new double[4][2]; //the vertices of the rectangle
	
	public Rectangle(double x1, double y1, double x2, double y2, double width)
	{
		double[] normal = MathUtil.normal(x1, y1, x2, y2);
		
		p[0][0] = normal[0]*width/2+x1;
		p[0][1] = normal[1]*width/2+y1;
		p[1][0] = -normal[0]*width/2+x1;
		p[1][1] = -normal[1]*width/2+y1;
		p[2][0] = -normal[0]*width/2+x2;
		p[2][1] = -normal[1]*width/2+y2;
		p[3][0] = normal[0]*width/2+x2;
		p[3][1] = normal[1]*width/2+y2;
	}
	public void drawRectangle(GL gl)
	{
		gl.glBegin(GL.GL_LINE_LOOP);
		for(int i = 0; i < p.length; i++)
		{
			gl.glVertex2d(p[i][0], p[i][1]);
		}
		gl.glEnd();
	}
}
