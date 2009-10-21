package mapEditor;

import javax.swing.*;
import java.awt.*;
import util.Camera;

public class EditorDisplay extends JPanel implements Runnable
{
	private static final long serialVersionUID = 1L;
	Camera c;
	MapEditor me;
	EditorObjectManager eom;
	
	public EditorDisplay(Camera c, MapEditor me, EditorObjectManager eom)
	{
		this.c = c;
		this.me = me;
		this.eom = eom;
		new Thread(this).start();
	}
	public void update(Graphics g)
	{
		paint(g);
	}
	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D)graphics;
		g.setColor(Color.green);
		g.fillRect(0, 0, getWidth(), getHeight());
		eom.drawObjects(g);
	}
	public void run()
	{
		for(;;)
		{
			repaint();
			try
			{
				Thread.sleep(50);
			}
			catch(InterruptedException e){}
		}
	}
}