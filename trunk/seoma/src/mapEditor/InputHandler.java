package mapEditor;

import java.awt.event.*;
import java.util.HashSet;


public class InputHandler implements KeyListener, MouseListener
{
	private static final long serialVersionUID = 1L;
	private HashSet<Character> keys = new HashSet<Character>();
	
	public InputHandler()
	{

	}
	
	public void keyPressed(KeyEvent e)
	{
		if(!keys.contains(e.getKeyChar()))
		{
			keys.add(e.getKeyChar());
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		keys.remove(e.getKeyChar());
	}
	
	public void keyTyped(KeyEvent e){}
	
	public boolean getKeyStatus(char c)
	{
		return keys.contains(c);
	}

	public void mouseClicked(MouseEvent arg0){}
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}

	public void mousePressed(MouseEvent me)
	{

	}

	public void mouseReleased(MouseEvent me)
	{

	}
}
