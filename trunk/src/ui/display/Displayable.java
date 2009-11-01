package ui.display;

import javax.media.opengl.GL;

public interface Displayable
{
	public void display(GL gl, int viewWidth, int viewHeight);
}
