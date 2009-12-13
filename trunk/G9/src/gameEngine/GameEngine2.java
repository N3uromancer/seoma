package gameEngine;

import java.util.HashMap;

import javax.media.opengl.GL;

import ai.AI;

import mapEditor.Map;

import gameEngine.ai.computerAI.computerAIs.*;
import gameEngine.ai.humanAI.BasicHumanAI;
import gameEngine.world.World;
import gameEngine.world.owner.Owner;
import ui.UIFrame;
import ui.display.Displayable;
import ui.userIO.UserInputInterpreter;

/**
 * handles and routes all user input, runs the main game thread
 * @author Jack
 *
 */
public class GameEngine2 extends UserInputInterpreter implements Runnable, Displayable
{
	World w;
	HashMap<Owner, AI> ais = new HashMap<Owner, AI>();
	Owner o; //the owner of the game engine, all user inputs are attributed to it
	boolean networked;
	
	public GameEngine2(boolean networked)
	{
		this.networked = networked;
		double[] c1 = {1, 0, 0};
		double[] c2 = {0, 0, 1};
		Owner[] owners = {new Owner("player 1", c1), new Owner("player 2", c2)};
		String[] startingUnits = {"tank"};
		//String[] startingUnits = {"factory s1", "builder s1"};
		//StartSettings ss = new StartSettings(700, 700, owners, startingUnits);
		
		
		StartSettings ss = new StartSettings(500, 500, new Owner[]{new Owner("player 1", c1)}, startingUnits);
		
		w = new World(ss);
		o = owners[0];
		
		ais.put(owners[0], new BasicHumanAI(owners[0], w));
		ais.put(owners[1], new TesterAI(owners[1], w));
		
		//new Thread(this).start();
	}
	public void keyAction(char c, boolean pressed)
	{
		
	}
	public void mouseAction(int x, int y, boolean pressed, boolean rightClick)
	{
		if(!networked)
		{
			ais.get(o).interpretMouseClick(x, y, pressed, rightClick);
		}
	}
	public void mouseMotionAction(int x, int y, boolean dragged, boolean rightClick)
	{
		if(!networked)
		{
			ais.get(o).interpretMouseMove(x, y, dragged, rightClick);
		}
	}
	public void run()
	{
		long pauseTime = 20;
		long start;
		long diff;
		
		long tdiff = 0;
		for(;;)
		{
			start = System.currentTimeMillis();
			w.updateWorld(tdiff/1000.0, ais);
			//w.updateWorld(1, ais); //set pause time to 200 to see lag
			diff = System.currentTimeMillis()-start;
			if(pauseTime - diff > 0)
			{
				try
				{
					Thread.sleep(pauseTime-diff);
				}
				catch(InterruptedException e){}
			}
			tdiff = System.currentTimeMillis()-start;
		}
	}
	public void display(GL gl, int viewWidth, int viewHeight)
	{
		int x = 0;
		int y = 0;
		w.drawWorld(x, y, viewWidth, viewHeight, gl);
		ais.get(o).drawUI(gl);
	}
	public static void main(String[] args)
	{
		GameEngine2 ge = new GameEngine2(false);
		new UIFrame(ge, ge);
		new Thread(ge).start();
	}
}
