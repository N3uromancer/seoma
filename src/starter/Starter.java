package starter;

import gameEngine.GameEngine;
import gameEngine.world.World;
import gameEngine.world.owner.Owner;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import portability.PortUtils;

import ui.UIFrame;
import ui.display.DisplayManager;
import ui.userIO.userInput.UserInput;

import ai.AI;

public class Starter {
	public static World startGameGUI(final HashMap<Owner, AI> ais, final Owner[] owners, long randSeed)
	{
		final World w = new World(owners, ais, randSeed);
		w.setCameraAI(ais.get(owners[0]));
		
		/* We only need this for GUI mode */
		if (PortUtils.runningFromJar())
			PortUtils.prepareNativeLibraries("lib");
		else
			PortUtils.prepareNativeLibraries(".."+File.separator+"seoma3"+File.separator+"lib");
		
		GLCanvas c = new GLCanvas(new GLCapabilities());
		final GameEngine ge = new GameEngine(false, c)
		{
			public void updateGame(double tdiff, HashMap<Byte, HashMap<Class<? extends UserInput>, ArrayList<UserInput>>> ui)
			{
				w.updateWorld(tdiff, ui);
				//w.updateWorld(tdiff, ui);
				//w.updateWorld(tdiff, ui);
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
		
		return w;
	}
	public static World startGameConsole(final HashMap<Owner, AI> ais, final Owner[] owners, long randSeed)
	{
		final World w = new World(owners, ais, randSeed);
		
		final GameEngine ge = new GameEngine(false, null)
		{
			public void updateGame(double tdiff, HashMap<Byte, HashMap<Class<? extends UserInput>, ArrayList<UserInput>>> ui)
			{
				w.updateWorld(tdiff, ui);
				//w.updateWorld(tdiff, ui);
				//w.updateWorld(tdiff, ui);
			}
		};
		
		return w;
	}
}
