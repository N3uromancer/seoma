package mapEditor.mapEditorMenuBar;

import gameEngine.world.World;

import java.awt.*;

public class MapEditorMenuBar extends MenuBar
{
	private static final long serialVersionUID = 1L;

	public MapEditorMenuBar(Frame f, World m)
	{
		add(new FileMenu(f, m));
		add(new EditMenu(m));
		add(new PropertiesMenu(f, m));
	}
}
