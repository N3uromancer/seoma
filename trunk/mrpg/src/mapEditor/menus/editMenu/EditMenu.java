package mapEditor.menus.editMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gameEngine.world.World;

import javax.swing.*;

import mapEditor.MapEditor;

public class EditMenu extends JMenu
{
	private static final long serialVersionUID = 1L;

	public EditMenu(MapEditor editor, JFrame frame, World w)
	{
		super("Edit");
		final MapEditor me = editor;
		final JFrame owner = frame;

		getPopupMenu().setLightWeightPopupEnabled(false);
		JMenuItem placeTerrain = new JMenuItem("Place Terrain");
		placeTerrain.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				new TerrainPlacementDialog(me, owner);
			}
		});
		add(placeTerrain);
	}
}
