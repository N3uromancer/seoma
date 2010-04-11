package world.modifier;

import geom.Boundable;

import java.awt.Graphics2D;

public interface Drawable extends Boundable
{
	public void draw(Graphics2D g);
}
