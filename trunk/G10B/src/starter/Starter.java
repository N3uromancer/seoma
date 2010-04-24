package starter;

import world.World;
import world.unit.Unit;
import world.unit.action.MoveAction;
import display.Camera;
import display.Display;

public class Starter
{
	public static void main(String[] args)
	{
		World w = new World();
		
		double[] s1 = {0, 0};
		double[] s2 = {800, 800};
		double width = 200;
		double height = 200;
		int units = 100;
		for(int i = 0; i < units; i ++)
		{
			Unit u = new Unit(new double[]{s1[0]+width*Math.random(), s1[1]+height*Math.random()});
			MoveAction move = new MoveAction(u, new double[]{900, 900});
			u.setAction(move);
			w.registerObject(u);
		}
		for(int i = 0; i < units; i ++)
		{
			Unit u = new Unit(new double[]{s2[0]+width*Math.random(), s2[1]+height*Math.random()});
			MoveAction move = new MoveAction(u, new double[]{100, 100});
			u.setAction(move);
			w.registerObject(u);
		}
		
		Camera c = new Camera(new double[]{0, 0}, 0, 0);
		Display d = new Display(w, c);
		c.setViewBounds(d.getDisplayMode().getWidth(), d.getDisplayMode().getHeight());
		
		long start = System.currentTimeMillis();
		long diff = 0; //time passed between updates
		long updateTime; //time it takes to update the game and display it
		long totalUpdateTime = 0; //total time spent updating the game
		long updates = 0;
		
		long sleepTime = 20;
		for(;;)
		{
			diff = System.currentTimeMillis()-start;
			start = System.currentTimeMillis();
			c.updateCamera(diff/1000.);
			w.updateWorld(diff/1000.);
			d.displayWorld();
			updateTime = System.currentTimeMillis()-start;
			totalUpdateTime+=updateTime;
			updates++;
			if(updateTime < sleepTime)
			{
				try
				{
					Thread.sleep(sleepTime-updateTime);
				}
				catch(InterruptedException e){}
			}
			if(updates % 100 == 0)
			{
				System.out.println(updates*1000./totalUpdateTime+"\t updates/sec");
				
				//to maintain a rolling average
				updates = 0;
				totalUpdateTime = 0;
				
				/*for(int i = 0; i < 30; i++)
				{
					Shot s = new Shot(new double[]{Math.random()*2000, Math.random()*2000}, new double[]{170, 170}, 40);
					Explosion e = new Explosion(s, .55);
					w.registerObject(e);
				}*/
			}
			//System.out.println(updates);
		}
	}
}
