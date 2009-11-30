package updateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;

import ui.userIO.userInput.UserInput;

/**
 * manages the updating of everything that can be updated, drawing is
 * included in updating
 * @author Jack
 *
 */
public class UpdateManager
{
	ArrayList<Updater> u = new ArrayList<Updater>(); //array list because updaters more permanent
	
	public void registerUpdater(Updater updater)
	{
		u.add(updater);
	}
	public void drawAll(double x, double y, double width, double height, GL gl)
	{
		Iterator<Updater> i = u.iterator();
		while(i.hasNext())
		{
			i.next().drawAll(x, y, width, height, gl);
		}
	}
	/**
	 * udates all registered updaters by the passed time difference
	 * @param tdiff the time difference
	 */
	public void updateAll(double tdiff, HashMap<Byte, ArrayList<UserInput>> ui)
	{
		Iterator<Updater> i = u.iterator();
		while(i.hasNext())
		{
			Updater u = i.next();
			u.update(this, tdiff, ui);
			if(u.isDead())
			{
				i.remove();
			}
		}
	}
}
