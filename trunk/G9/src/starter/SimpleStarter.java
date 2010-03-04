package starter;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import portability.PortUtils;
import ai.AI;
import ui.UIFrame;
import ui.display.DisplayManager;
import ui.userIO.userInput.UserInput;
import gameEngine.GameEngine;
import ai.computerAI.*;
import ai.computerAI.computerEasy.ComputerEasy;
import world.World;
import world.owner.Owner;

public class SimpleStarter
{
	public static void main(String[] args)
	{
		final Owner[] owners = StarterHelper.getOwners(4);
		String mapPath = System.getProperty("user.dir")+System.getProperty("file.separator")+
		System.getProperty("file.separator")+"maps"+System.getProperty("file.separator")+
		"SovietRussia.map";
		
		for (int i = 0; i < owners.length; i++)
		{
			System.out.print(owners[i].getName() + " - ");
			System.out.print("{ ");
			for (int j = 0; j < 3; j++)
				System.out.print((int)owners[i].getColor()[j] + " ");
			System.out.println("}");
		}

		if (!PortUtils.runningFromJar())
			PortUtils.prepareNativeLibraries(".."+File.separator+"seoma3"+File.separator+"lib");
		
		HashMap<Owner, AI> ais = new HashMap<Owner, AI>();
		ais.put(owners[0], new StalinArmy(owners[0]));
		ais.put(owners[1], new YortSepar(owners[1]));
		ais.put(owners[2], new YortSepar(owners[2]));
		ais.put(owners[3], new YortSepar(owners[3]));
		final World w = new World(owners, ais, System.currentTimeMillis(), mapPath);
		w.setCameraAI(ais.get(owners[0]));
		
		GLCanvas c = new GLCanvas(new GLCapabilities());;
		final GameEngine ge = new GameEngine(false, c)
		{
			public void updateGame(double tdiff, HashMap<Byte, HashMap<Class<? extends UserInput>, ArrayList<UserInput>>> ui)
			{
				for (int i = 0; i < 3; i++)
					w.updateWorld(.03, ui);
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
