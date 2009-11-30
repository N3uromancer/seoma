package mapEditor.menus.fileMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mapEditor.MapEditor;

import gameEngine.world.World;

public class FileMenu extends JMenu
{
	private static final long serialVersionUID = 1L;

	public FileMenu(MapEditor me, JFrame o, World w)
	{
		super("File");
		final JFrame owner = o;
		getPopupMenu().setLightWeightPopupEnabled(false);
		
		JMenuItem save = new JMenuItem("Save");
		JMenuItem load = new JMenuItem("Load");
		
		add(save);
		add(load);
		addSeparator();
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				new ExitDialog(owner);
			}
		});
		add(exit);
	}
}
