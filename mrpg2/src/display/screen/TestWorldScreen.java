package display.screen;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import world.World;
import world.unit.MovingUnit;
import display.Display;

/**
 * loads a blank and empty world for units to move around in
 * @author Jack
 *
 */
public class TestWorldScreen implements Screen, Runnable
{
	JComponent[] c;
	Display d;
	World w;
	
	public TestWorldScreen(Display d)
	{
		this.d = d;
		
		c = new JComponent[1];
		JButton exit = new JButton("Exit");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		exit.setBounds(ScreenConstants.backButtonLocation[0], ScreenConstants.backButtonLocation[1], 200, 40);
		c[0] = exit;
		
		w = new World();
		w.addGameObject(new MovingUnit(new double[]{50, 50}, 50, 50, 200, 200));
		
		new Thread(this).start();
	}
	public void run()
	{
		for(;;)
		{
			w.updateWorld(30);
			d.updateDisplay();
			try
			{
				Thread.sleep(30);
			}
			catch(InterruptedException e){}
		}
	}
	public void displayScreen(Graphics2D g, DisplayMode dm)
	{
		w.drawWorld(g, dm.getWidth(), dm.getHeight());
	}
	public JComponent[] getComponents()
	{
		return c;
	}
}
