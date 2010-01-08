package gameEngine.world.unit;

import javax.media.opengl.GL;

import gameEngine.world.owner.Owner;
import gameEngine.world.weapon.Weapon;

public abstract class Building extends Unit
{
	public Building(String name, Owner o, double x, double y, double width, double height, double life, Weapon w, double buildTime, double cost)
	{
		super(name, o, x, y, width, height, life, 0, w, buildTime, cost);
	}
	public void draw(GL gl)
	{
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
