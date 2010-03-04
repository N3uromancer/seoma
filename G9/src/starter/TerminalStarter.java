package starter;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import portability.PortUtils;

import ui.UIFrame;
import ui.display.DisplayManager;
import ui.userIO.userInput.UserInput;
import world.World;
import world.owner.Owner;

import gameEngine.GameEngine;
import io.SimpleClassLoader;
import ai.AI;

public class TerminalStarter {
	private static void usage()
	{
		System.out.println("Usage: TerminalStarter -ai -map [-dbg -?]");
		System.exit(-1);
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{	
		boolean debug = false;
		SimpleClassLoader cl = new SimpleClassLoader();
		ArrayList<Class<? extends AI>> aicList = new ArrayList<Class<? extends AI>>();
		String mapPath = null;
		final Owner[] owners;
		HashMap<Owner, AI> aiHash = new HashMap<Owner, AI>();
		
		if (args.length == 0)
			usage();
		
		if (!PortUtils.runningFromJar())
			PortUtils.prepareNativeLibraries(".."+File.separator+"seoma3"+File.separator+"lib");
		else
			PortUtils.prepareNativeLibraries("lib");
		
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equalsIgnoreCase("-?"))
				usage();
			
			if (args[i].equalsIgnoreCase("-dbg"))
				debug = true;
			
			if (args[i].equalsIgnoreCase("-ai"))
			{
				if (i == args.length-1)
				{
					System.out.println("Missing AI class");
					System.exit(-1);
				}
				
				try {
					aicList.add(cl.loadClassFromFile(new File(args[i+1])));
				} catch (IOException e) {
					if (debug)
						e.printStackTrace();
					else
						System.out.println("Invalid class: "+args[i+1]);
					System.exit(-1);
				}
			}
			
			if (args[i].equalsIgnoreCase("-map"))
			{
				if (i == args.length-1)
				{
					System.out.println("Missing map path");
					System.exit(-1);
				}
				
				mapPath = args[i+1];
			}
		}
		
		if (aicList.size() == 0)
		{
			System.out.println("No AIs specified");
			usage();
			System.exit(-1);
		}
		
		if (mapPath == null)
		{
			System.out.println("No map specified");
			usage();
			System.exit(-1);
		}
		
		owners = StarterHelper.getOwners(aicList.size());
		
		for (int i = 0; i < owners.length; i++)
		{
			AI ai = null;
			
			try {
				ai = (AI)cl.constructObjectFromClass(aicList.get(i), new Class[]{Owner.class}, new Object[]{owners[i]});
			} catch (Exception e) {
				if (debug)
					e.printStackTrace();
				else
					System.out.println("Failed to create AI class");
				System.exit(-1);
			}
			
			aiHash.put(owners[i], ai);
		}
		
		final World w = new World(owners, aiHash, System.currentTimeMillis(), mapPath);
		w.setCameraAI(aiHash.get(owners[0]));
		
		GLCanvas c = new GLCanvas(new GLCapabilities());
		final GameEngine ge = new GameEngine(false, c)
		{
			public void updateGame(double tdiff, HashMap<Byte, HashMap<Class<? extends UserInput>, ArrayList<UserInput>>> ui)
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
