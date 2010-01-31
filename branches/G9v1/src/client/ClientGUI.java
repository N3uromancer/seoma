package client;

import gameEngine.world.owner.Owner;
import io.SimpleClassLoader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import server.Game;
import starter.Starter;

import ai.AI;

import com.enterprisedt.net.ftp.FTPClient;

public class ClientGUI implements ActionListener, Runnable {
	JComboBox c;
	JFrame f;
	Object[] obj;
	Thread t;
	
	final static String pending = "Downloading game information from server...";
	final static String failed = "Failed to download game data";
	
	final static String recordsPath = "records/records.bin";
	
	public void run()
	{
		try {
			File f = downloadFile(recordsPath);
			if (f == null)
			{
				//easy way to jump into the exception handler
				throw new Exception();
			}
			ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(f));
			obj = (Object[]) objIn.readObject();
		} catch (Exception e) {
			c.removeItem(pending);
			c.addItem(failed);
			return;
		}
		String[] games = new String[obj.length];
		int i = 0;
		c.removeItem(pending);
		for (Object o : obj)
		{
			Game g = (Game)o;
			for (int j = 0; j < g.aiFiles.length; i++)
			{
				games[i] += (j != 0) ? " vs. " : "" + g.aiFiles[j];
			}
			games[i] += " (Winner: "+g.aiFiles[g.winner]+")";
			c.addItem(games[i++]);
		}
		c.addActionListener(this);
	}
	
	public ClientGUI() throws IOException, ClassNotFoundException {
		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		f.setTitle("Super RTS G9 Match Replay - AI Selector");
		f.setSize(500, 100);
		
		c = new JComboBox();
		c.addItem(pending);
		c.setEnabled(false);
		f.getContentPane().add(c);
		f.setVisible(true);
		t = new Thread(this);
		t.start();
	}
	public static void main(String[] args) {
		try {
			new ClientGUI();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private File downloadFile(String path)
	{
		FTPClient fc = new FTPClient();
		try {
			fc.setRemoteHost("cgutman.no-ip.org");
			fc.connect();
			fc.login("Anonymous", "");
			File f = File.createTempFile("G9", "Records");
			fc.get(new FileOutputStream(f), recordsPath);
			fc.quit();
			return f;
		} catch (Exception e) {
			return null;
		}
	}
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getID() == 1001)
		{
			Game g = (Game)obj[c.getSelectedIndex()];
			Class[] c = new Class[g.aiFiles.length];
			SimpleClassLoader cl = new SimpleClassLoader();
			HashMap<Owner, AI> hm = new HashMap<Owner, AI>();
			final Owner[] o = g.o;
			for (int i = 0; i < c.length; i++)
			{
				File f = downloadFile(g.aiFiles[i]);
				if (f == null)
				{
					//FIXME: Do something here!
					System.out.println("Failed to download AI");
					System.exit(-1);
				}
				
				try {
					hm.put(o[i], (AI) cl.constructObjectFromClass(cl.loadClassFromFile(f), new Class[]{Owner.class}, new Object[] {o[i]}));
				} catch (Exception e) {
					//FIXME: Do something here!
					System.out.println("Failed to construct AI");
					System.exit(-2);
				}
			}
			f.setVisible(false);
			Starter.startGameGUI(hm, o, g.seed);
		}
	}
}
