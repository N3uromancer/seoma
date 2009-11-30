package mapEditor.menus;

import gameEngine.world.World;

import javax.swing.*;

import mapEditor.MapEditor;
import mapEditor.menus.editMenu.EditMenu;
import mapEditor.menus.fileMenu.FileMenu;
import mapEditor.menus.propertiesMenu.PropertiesMenu;
import mapEditor.menus.viewMenu.ViewMenu;

public class MapEditorMenuBar extends JMenuBar
{
	private static final long serialVersionUID = 1L;

	public MapEditorMenuBar(MapEditor me, JFrame owner, World w)
	{
		add(new FileMenu(me, owner, w));
		add(new EditMenu(me, owner, w));
		add(new ViewMenu(w));
		add(new PropertiesMenu(me, owner, w));
	}
}
