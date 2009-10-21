package box;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;

import com.sun.sgs.app.ManagedObject;

public class Box implements Serializable, ManagedObject
{
	private static final long serialVersionUID = 1L;
	private final int width = 50;
	double[] s = new double[2];
	
	public Box(double x, double y)
	{
		s[0] = x;
		s[1] = y;
	}
	public void translate(double[] t)
	{
		s[0]+=t[0];
		s[1]+=t[1];
	}
	public void drawBox(Graphics2D g)
	{
		g.setColor(Color.red);
		g.fillRect((int)s[0]-width/2, (int)s[1]-width/2, width, width);
		g.setColor(Color.black);
		g.drawRect((int)s[0]-width/2, (int)s[1]-width/2, width, width);
	}
}
