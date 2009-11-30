package mapEditor.menus.viewMenu;

import gameEngine.world.World;


import javax.swing.JMenu;

public class ViewMenu extends JMenu
{
	private static final long serialVersionUID = 1L;

	public ViewMenu(World w)
	{
		super("View");
		getPopupMenu().setLightWeightPopupEnabled(false);
	}
}
