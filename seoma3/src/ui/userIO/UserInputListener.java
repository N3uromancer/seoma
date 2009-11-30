package ui.userIO;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * listens for all user input, mouse events, key events, etc., sends the input
 * over to the associated interpreter
 * 
 * in inputs that include a coordinate (such as mouse press, etc), coordinates are given
 * in the opengl coordinate system
 * @author Jack
 *
 */
public class UserInputListener implements KeyListener, MouseListener, MouseMotionListener
{
	UserInputInterpreter uii;
	int height = 0;
	
	public void keyPressed(KeyEvent e)
	{
		if(uii != null)
		{
			uii.keyAction(e.getKeyChar(), true);
		}
	}
	public void keyReleased(KeyEvent e)
	{
		if(uii != null)
		{
			uii.keyAction(e.getKeyChar(), false);
		}
	}
	public void keyTyped(KeyEvent arg0){}
	public void mouseClicked(MouseEvent e)
	{
		if(uii != null)
		{
			uii.mouseClickAction(e.getPoint().x, height-e.getPoint().y, e.getButton()==MouseEvent.BUTTON3);
		}
	}
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}
	public void mousePressed(MouseEvent e)
	{
		if(uii != null)
		{
			uii.mouseAction(e.getPoint().x, height-e.getPoint().y, true, e.getButton()==MouseEvent.BUTTON3);
		}
	}
	public void mouseReleased(MouseEvent e)
	{
		if(uii != null)
		{
			uii.mouseAction(e.getPoint().x, height-e.getPoint().y, false, e.getButton()==MouseEvent.BUTTON3);
		}
	}
	public void setUserInputInterpreter(UserInputInterpreter uii)
	{
		this.uii = uii;
	}
	/**
	 * sets the screen height, this is needed to transform the y position of mouse
	 * clicks, which are recorded in the java coord system, to the gl coord system
	 * @param sh the height of the viewing area
	 */
	public void setViewHeight(int sh)
	{
		height = sh;
	}
	public void mouseDragged(MouseEvent e)
	{
		uii.mouseMoveAction(e.getPoint().x, height-e.getPoint().y, true, e.getButton()==MouseEvent.BUTTON3);
	}
	public void mouseMoved(MouseEvent e)
	{
		uii.mouseMoveAction(e.getPoint().x, height-e.getPoint().y, false, e.getButton()==MouseEvent.BUTTON3);
	}
}
