package updateManager;

import gameEngine.world.Element;

import javax.media.opengl.GL;

public interface Drawable extends Element
{
	public void draw(GL gl);
}
