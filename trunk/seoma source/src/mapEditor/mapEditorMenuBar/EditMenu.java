package mapEditor.mapEditorMenuBar;

import gameEngine.world.World;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class EditMenu extends Menu
{
	private static final long serialVersionUID = 1L;
	World m;
	public EditMenu(World map)
	{
		super("Edit");
		this.m = map;
		
		MenuItem clearPoly = new MenuItem("Clear Polygons");
		clearPoly.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				m.clearPolygons();
			}
		});
		add(clearPoly);
	}
}
