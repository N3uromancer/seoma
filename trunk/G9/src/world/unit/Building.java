package world.unit;

import javax.media.opengl.GL;

import world.owner.Owner;
import world.weapon.Weapon;

public abstract class Building extends Unit
{
	public Building(String name, Owner o, double x, double y, double width, double height, double life, Weapon w, double buildTime, double cost)
	{
		super(name, o, x, y, width, height, life, 0, w, buildTime, cost);
	}
	public void draw(GL gl)
	{
		double x = bounds.getLocation()[0];
		double y = bounds.getLocation()[1];
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		
		double d = 0; //depth
		double[] c = o.getColor();
		gl.glColor4d(c[0], c[1], c[2], 1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3d(x, y, d);
		gl.glVertex3d(x+width, y, d);
		gl.glVertex3d(x+width, y+height, d);
		gl.glVertex3d(x, y+height, d);
		gl.glEnd();
		
		if(a.size() > 0)
		{
			a.peek().drawAction(gl);
		}
		
		if(selected)
		{
			drawSelection(gl);
		}
	}
}
