package gameEngine;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GLCanvas;

import ui.userIO.UserInputInterpreter;
import ui.userIO.userInput.*;
import updateManager.UpdateManager;

public class GameEngine implements Runnable, UserInputInterpreter
{
	/**
	 * the owner id assigned to this game engine for the purposes of determining
	 * the origin of user inputs
	 */
	byte owner;
	boolean networked;
	/**
	 * stores user input when not networked
	 * key=owner id, value=list of input by that user
	 */
	HashMap<Byte, ArrayList<UserInput>> ui = new HashMap<Byte, ArrayList<UserInput>>();
	GLCanvas c;
	UpdateManager um;
	
	/**
	 * creates a new game engine
	 * @param networked true if the game engine is to communicate with a server over a network,
	 * false otherwise, if false then the owner id is defaultly set to 0 and an update thread is
	 * immediately started after initialization
	 * @param um
	 * @param c
	 */
	public GameEngine(boolean networked, UpdateManager um, GLCanvas c)
	{
		this.networked = networked;
		this.c = c;
		this.um = um;
		
		if(!networked)
		{
			owner = 0;
			new Thread(this).start();
		}
	}
	/**
	 * gets the owner of this game engine, all user inputs generated in this
	 * engine are attributed to this owner
	 * @return gets teh owner of this game engine
	 */
	public byte getOwner()
	{
		return owner;
	}
	/**
	 * updates the game, only runs if game is not networked, clears
	 * the user input buffer every iteration
	 */
	public void run()
	{
		long diff;
		long start = System.currentTimeMillis();
		for(;;)
		{
			try
			{
				Thread.sleep(30);
			}
			catch(InterruptedException e){}
			diff = System.currentTimeMillis()-start;
			start = System.currentTimeMillis();
			updateGame(diff/1000.0, new HashMap<Byte, ArrayList<UserInput>>(ui));
			ui = new HashMap<Byte, ArrayList<UserInput>>();
		}
	}
	public void updateGame(double tdiff, HashMap<Byte, ArrayList<UserInput>> ui)
	{
		um.updateAll(tdiff, ui);
		c.repaint();
	}
	public void keyAction(char c, boolean pressed)
	{
		
	}
	public void mouseAction(int x, int y, boolean pressed, boolean rightClick)
	{
		if(!networked)
		{
			MouseInput mi = null;
			if(pressed)
			{
				mi = new MousePress(owner, x, y, rightClick);
				//System.out.println("mouse press registered");
			}
			else
			{
				mi = new MouseRelease(owner, x, y, rightClick);
				//System.out.println("mouse release registered");
			}
			registerUserInput(mi);
		}
	}
	private void registerUserInput(UserInput userInput)
	{
		if(ui.get(owner) != null)
		{
			ui.get(owner).add(userInput);
		}
		else
		{
			ArrayList<UserInput> temp = new ArrayList<UserInput>();
			temp.add(userInput);
			ui.put(owner, temp);
		}
	}
	public void mouseClickAction(int x, int y, boolean rightClick)
	{
		if(!networked)
		{
			registerUserInput(new MouseClick(owner, x, y, rightClick));
		}
	}
	public void mouseMoveAction(int x, int y, boolean dragged, boolean rightClick)
	{
		
	}
	public void keyTyped(char c)
	{
		if(!networked)
		{
			registerUserInput(new KeyTyped(owner, c));
		}
	}
}
