package mapEditor;

import javax.swing.*;
import java.awt.*;
import util.Camera;

public class EditorDisplay extends JPanel implements Runnable
{
	private static final long serialVersionUID = 1L;
	MapEditor me;
	Camera c;
	
	public EditorDisplay(MapEditor me, Camera c)
	{
		this.me = me;
		this.c = c;
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