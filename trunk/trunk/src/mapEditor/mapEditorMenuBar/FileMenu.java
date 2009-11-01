package mapEditor.mapEditorMenuBar;

import gameEngine.world.World;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.JFileChooser;

import mapEditor.Map;

public class FileMenu extends Menu
{
	private static final long serialVersionUID = 1L;
	World m;
	Frame f;
	
	public FileMenu(Frame frame, World map)
	{
		super("File");
		this.m = map;
		this.f = frame;
		
		MenuItem save = new MenuItem("Save");
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				File file = new File(System.getProperty("user.dir"));
				File map = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+m.getName()+".map");
				JFileChooser fc = new JFileChooser(file);
				fc.setSelectedFile(map);
				int returnValue = fc.showDialog(f, "Save World");
				if(returnValue == JFileChooser.APPROVE_OPTION)
				{
					File sf = fc.getSelectedFile();
					try
					{
						FileOutputStream fos = new FileOutputStream(sf);
						DataOutputStream dos = new DataOutputStream(fos);
						TextDialog td = new TextDialog(f, "Saving...");
						m.writeMap(dos);
						td.dispose();
					}
					catch(IOException a){}
				}
			}
		});
		MenuItem load = new MenuItem("Load");
		load.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				File file = new File(System.getProperty("user.dir"));
				JFileChooser fc = new JFileChooser(file);
				int returnValue = fc.showDialog(f, "Load World");
				if(returnValue == JFileChooser.APPROVE_OPTION)
				{
					File sf = fc.getSelectedFile();
					try
					{
						FileInputStream fis = new FileInputStream(sf);
						DataInputStream dis = new DataInputStream(fis);
						TextDialog td = new TextDialog(f, "Loading...");
						m.readMap(dis);
						td.dispose();
					}
					catch(IOException a){}
				}
			}
		});
		MenuItem exit = new MenuItem("Exit");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		
		add(save);
		add(load);
		addSeparator();
		add(exit);
	}
}
