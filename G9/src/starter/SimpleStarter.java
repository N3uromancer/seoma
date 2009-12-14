package starter;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import ai.AI;
import ui.UIFrame;
import ui.display.DisplayManager;
import ui.userIO.userInput.UserInput;
import gameEngine.GameEngine;
import gameEngine.StartSettings;
import ai.computerAI.*;
import ai.humanAI.BasicHumanAI;
import gameEngine.world.World;
import gameEngine.world.owner.Owner;

public class SimpleStarter
{
	public static void main(String[] args)
	{
		double[] c1 = {1, 0, 0};
		double[] c2 = {0, 0, 1};
		final Owner[] owners = {new Owner((byte)0, "player 1", c1), new Owner((byte)2, "player 2", c2)};
		String[] startingUnits = {"leader"};
		//String[] startingUnits = {"factory s1", "builder s1"};
		StartSettings ss = new StartSettings(700, 700, owners, startingUnits);
		
		
		//StartSettings ss = new StartSettings(500, 500, new Owner[]{new Owner("player 1", c1)}, startingUnits);

		HashMap<Owner, AI> ais = new HashMap<Owner, AI>();
		ais.put(owners[0], new RapestroyAI(owners[0]));
		ais.put(owners[1], new RapestroyAI(owners[1]));
		final World w = new World(ss, ais);
		
		GLCanvas c = new GLCanvas(new GLCapabilities());;
		final GameEngine ge = new GameEngine(false, c)
		{
			public void updateGame(double tdiff, HashMap<Byte, ArrayList<UserInput>> ui)
			{
				w.updateWorld(tdiff, ui);
			}
		};
		DisplayManager dm = new DisplayManager()
		{
			public void display(GL gl, int width, int height)
			{
				w.drawWorld(owners[0], width, height, gl);
			}
		};
		UIFrame f = new UIFrame(ge, c, dm);
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}
}
