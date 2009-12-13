package mapEditor.mapEditorMenuBar;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import mapEditor.Map;

public final class EditMenu extends Menu
{
	private static final long serialVersionUID = 1L;

	public EditMenu(Map m)
	{
		super("Edit");
		final Map map = m;
		
		MenuItem clearPoly = new MenuItem("Clear Polygons");
		clearPoly.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				map.clearPolygons();
			}
		});
		add(clearPoly);
		MenuItem clearResDep = new MenuItem("Clear Resouce Deposits");
		clearResDep.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				map.clearResourceDeposits();
			}
		});
		add(clearResDep);
	}
}
