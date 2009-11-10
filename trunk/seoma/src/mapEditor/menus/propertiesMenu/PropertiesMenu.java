package mapEditor.menus.propertiesMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gameEngine.world.World;

import javax.swing.*;

import mapEditor.MapEditor;

public class PropertiesMenu extends JMenu
{
	private static final long serialVersionUID = 1L;
	JFrame owner;
	MapEditor me;
	
	public PropertiesMenu(MapEditor editor, JFrame frame, World w)
	{
		super("Properties");
		owner = frame;
		me = editor;
		JMenuItem sourceSettings = new JMenuItem("Source Settings");
		sourceSettings.setToolTipText("sets the paths to the .class files used to make the game");
		sourceSettings.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				new SourceSettings(me, owner);
			}
		});
		add(sourceSettings);
	}
}
