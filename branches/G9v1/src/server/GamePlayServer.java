package server;

import gameEngine.world.World;
import gameEngine.world.owner.Owner;

import io.SimpleClassLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
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
	GamePlayThread currThread;
	File outLog, errLog;
	ArrayList<Game> g;
	
	final static String stdErrPath = "logs"+File.separatorChar+"stderr.log";
	final static String stdOutPath = "logs"+File.separatorChar+"stdout.log";
	
	public GamePlayServer() {
		System.out.print("GamePlayServer: starting...");
		serv = new HookServ("127.0.0.1", 10, "ai-server", "aisuperserver", this);
		cLoader = new SimpleClassLoader();
		ais = new ArrayList<AIListEntry>();
		g = new ArrayList<Game>();
		currThread = null;
		System.out.println("done");
		System.out.print("GamePlayServer: Moving stdout and stderr to server log...");
		outLog = new File(stdOutPath);
		errLog = new File(stdErrPath);

		try {
			if (!outLog.exists())
				outLog.createNewFile();
			
			if (!outLog.exists())
				errLog.createNewFile();
			
			PrintStream oldOut = System.out;
			
			System.setErr(new PrintStream(new FileOutputStream(errLog)));
			System.setOut(new PrintStream(new FileOutputStream(outLog)));
			
			oldOut.println("done");
		} catch (IOException e) {
			System.out.println("failed");
		}
		System.out.println("---- Server started ----");
		System.out.println("Waiting for files...");
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
			f.delete();
			return;
		}
		if (!f.getName().substring(f.getName().lastIndexOf(".")).equals(".class"))
		{
			System.out.println("Unrecognized file extension: "+f.getName().substring(f.getName().lastIndexOf(".")));
			f.delete();
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
		if (currThread != null)
			while (currThread.t.isAlive()) Thread.yield();
		
		ais.add(new AIListEntry(c, f.getName()));
		
		currThread = new GamePlayThread(cLoader, ais, g);
	}
}

class GamePlayThread implements Runnable {
	ArrayList<AIListEntry> ais;
	ArrayList<Game> games;
	SimpleClassLoader cLoader;
	Thread t;

	final static String recordPath = "records"+File.separatorChar+"records.bin";
	final static String userFriendlyPath = "records"+File.separatorChar+"records.txt";
	
	public GamePlayThread(SimpleClassLoader cLoader, ArrayList<AIListEntry> ais, ArrayList<Game> games)
	{
		this.ais = ais;
		this.cLoader = cLoader;
		this.games = games;
		
		File f = new File(recordPath);
		
		try {
			if (!f.exists())
				f.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		t = new Thread(this);
		t.start();
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
					//double[] c3 = {1, 0, 1};
					//double[] c4 = {1, 1, 0};
					final Owner[] owners = {new Owner((byte)0, "player 1", c1), new Owner((byte)2, "player 2", c2)};
					World w;
					AI ai1, ai2;
					long seed = System.currentTimeMillis();

					HashMap<Owner, AI> aiHashMap = new HashMap<Owner, AI>();
					try {
						ai1 = (AI) cLoader.constructObjectFromClass(aiEntry1.c, new Class[] {Owner.class}, new Object[] {owners[0]});
						ai2 = (AI) cLoader.constructObjectFromClass(aiEntry2.c, new Class[] {Owner.class}, new Object[] {owners[1]});
						
						aiHashMap.put(owners[0], ai1);
						aiHashMap.put(owners[1], ai2);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					
					System.out.println("Playing "+aiEntry1.c.getName()+" vs. "+aiEntry2.c.getName());
					w = Starter.startGameConsole(aiHashMap, owners, seed);
					
					int minCount = 0;
					while (!t.isInterrupted())
					{
						for (int i = 0; i < w.getOwners().length; i++)
						{
							System.out.println("Minute "+minCount+" - Owner "+i+": "+w.getUnitEngine().getUnitList(w.getOwners()[i]).size());
						}
						minCount++;
						
						if (w.getUnitEngine().getUnitList(owners[1]).size() == 0)
						{
							aiEntry1.wins.add(aiEntry2.c);
							aiEntry2.losses.add(aiEntry1.c);
							break;
						}
						else if (w.getUnitEngine().getUnitList(owners[0]).size() == 0)
						{
							aiEntry2.wins.add(aiEntry1.c);
							aiEntry1.losses.add(aiEntry2.c);
							break;
						}
						
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e) {}
					}
					
					saveRecord(seed, new AIListEntry[] {aiEntry1, aiEntry2}, owners);
				}
			}
		}
	}
	
	private void saveRecord(long seed, AIListEntry[] ais, Owner[] o)
	{
		String[] aiF = new String[ais.length];
		
		for (int i = 0; i < ais.length; i++)
			aiF[i] = ais[i].fName;
		
		games.add(new Game(seed, aiF, ais[0].wins.contains(ais[1].c) ? 0 : 1, o));
		
		Object[] gs = games.toArray();
		
		try {
			ObjectOutputStream stats = new ObjectOutputStream(new FileOutputStream(new File(recordPath)));
			
			stats.writeObject(gs);
			stats.flush();
			stats.close();
			
			System.out.println("Updated "+recordPath);
			
			PrintStream ps = new PrintStream(new FileOutputStream(new File(userFriendlyPath)));
			for (AIListEntry a : ais)
			{
				ps.println(a.fName + '\t' + a.wins.size() + '-' + a.losses.size());
			}
			ps.flush();
			ps.close();
			
			System.out.println("Updated "+userFriendlyPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class AIListEntry {
	Class c;
	ArrayList<Class> losses;
	ArrayList<Class> wins;
	String fName;
	
	public AIListEntry(Class c, String f)
	{
		this.c = c;
		losses = new ArrayList<Class>();
		wins = new ArrayList<Class>();
		this.fName = f;
	}
}
