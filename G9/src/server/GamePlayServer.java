package server;

import gameEngine.StartSettings;
import gameEngine.world.owner.Owner;

import io.SimpleClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import ai.AI;

import starter.Starter;
import uploadHookServer.HookCallbacks;
import uploadHookServer.HookServ;

public class GamePlayServer extends Thread implements HookCallbacks {
	HookServ serv;
	SimpleClassLoader cLoader;
	ArrayList<AIListEntry> ais;
	
	public GamePlayServer() {
		System.out.print("GamePlayServer: starting...");
		serv = new HookServ("gutman.dyndns.org", 10, "AI-SERVER", "SERVER-ACCT", this);
		cLoader = new SimpleClassLoader();
		ais = new ArrayList<AIListEntry>();
		System.out.println("done");
		System.out.println("Waiting for files...");
	}
	
	public void run() {
			for (AIListEntry aiEntry1 : ais)
			{
				for (AIListEntry aiEntry2 : ais)
				{
					if (aiEntry1 != aiEntry2 && !aiEntry1.losses.contains(aiEntry2.c) &&
						!aiEntry1.wins.contains(aiEntry2.c))
					{
						/* Shamefully copied from SimpleStarter */
						double[] c1 = {1, 0, 0};
						double[] c2 = {0, 0, 1};
						final Owner[] owners = {new Owner((byte)0, "player 1", c1), new Owner((byte)2, "player 2", c2)};
						String[] startingUnits = {"leader"};
						//String[] startingUnits = {"factory s1", "builder s1"};
						StartSettings ss = new StartSettings(700, 700, owners, startingUnits);
						//StartSettings ss = new StartSettings(500, 500, new Owner[]{new Owner("player 1", c1)}, startingUnits);

						HashMap<Owner, AI> aiHashMap = new HashMap<Owner, AI>();
						try {
							aiHashMap.put(owners[0], (AI) cLoader.constructObjectFromClass(aiEntry1.c, new Class[] {Owner.class}, new Object[] {owners[0]}));
							aiHashMap.put(owners[1], (AI) cLoader.constructObjectFromClass(aiEntry2.c, new Class[] {Owner.class}, new Object[] {owners[1]}));
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
						
						/* FIXME: We don't track stats properly yet */
						aiEntry1.wins.add(aiEntry2.c);
						aiEntry2.wins.add(aiEntry1.c);
						
						System.out.println("Playing "+aiEntry1.c.getName()+" vs. "+aiEntry2.c.getName());
						Starter.startGame(aiHashMap, ss, owners);
						
						/* TODO: Record results */
					}
				}
			}
		}
	
	public static void main(String[] args) {
		new GamePlayServer();
	}

	public void exception(Exception e) {
		e.printStackTrace();
		
		System.out.println("Exception thrown: Exiting...");
		System.exit(-1);
	}

	public void fileReceived(File f) {
		if (f.getName().indexOf(".") == -1)
		{
			System.out.println("No file extension");
			return;
		}
		if (!f.getName().substring(f.getName().lastIndexOf(".")).equals(".class"))
		{
			System.out.println("Unrecognized file extension: "+f.getName().substring(f.getName().lastIndexOf(".")));
			return;
		}
		
		Class c;
		try {
			c = cLoader.loadClassFromFile(f);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("Loaded class from: "+f.getName());
		
		/* Wait for the worker to die so we don't get a concurrent modification exception */
		while (this.isAlive()) Thread.yield();
		
		ais.add(new AIListEntry(c));
		
		start();
	}
}

class AIListEntry {
	Class c;
	ArrayList<Class> losses;
	ArrayList<Class> wins;
	
	public AIListEntry(Class c)
	{
		this.c = c;
		losses = new ArrayList<Class>();
		wins = new ArrayList<Class>();
	}
}
