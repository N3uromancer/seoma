package mapEditor.mapEditorMenuBar;

import java.awt.*;

import mapEditor.Map;

public class MapEditorMenuBar extends MenuBar
{
	public MapEditorMenuBar(Frame f, Map m)
	{
		add(new FileMenu(f, m));
		add(new EditMenu(m));
		add(new PropertiesMenu(f, m));
	}
}
