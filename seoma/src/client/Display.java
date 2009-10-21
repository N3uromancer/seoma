package client;

import javax.swing.*;
import java.awt.*;

public class Display extends JPanel implements Runnable
{
	private static final long serialVersionUID = 1L;
	BoxClient bc;
	
	public Display(BoxClient bc)
	{
		this.bc = bc;
		new Thread(this).start();
	}
	public void update(Graphics g)
	{
		paint(g);
	}
	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D)graphics;
		g.setColor(Color.orange);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for(int i = 0; i < bc.getUnits().length; i++)
		{
			if(bc.getUnits()[i] != null)
			{
				bc.getUnits()[i].drawBox(g);
			}
		}
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
