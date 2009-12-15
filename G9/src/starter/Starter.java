package starter;

import gameEngine.GameEngine;
import gameEngine.StartSettings;
import gameEngine.world.World;
import gameEngine.world.owner.Owner;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import ui.UIFrame;
import ui.display.DisplayManager;
import ui.userIO.userInput.UserInput;

import ai.AI;

public class Starter {
	public static void startGame(final HashMap<Owner, AI> ais, StartSettings ss, final Owner[] o)
	{
		final World w = new World(ss, ais);
		
		GLCanvas c = new GLCanvas(new GLCapabilities());
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
				w.drawWorld(o[0], width, height, gl);
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
