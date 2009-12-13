package gameEngine.world.action;

import java.util.ArrayList;

import javax.media.opengl.GL;

/**
 * defines a list of actions as a single action; ex gathering resources
 * consists of moving to the resource, loading it, then bringing it back
 * @author Jack
 *
 */
public class ActionList extends Action
{
	private ArrayList<Action> a = new ArrayList<Action>();
	private int index = 0; //the index of the action that the list is on
	
	public ActionList(String name)
	{
		super(name);
	}
	public void addActionToList(Action action)
	{
		a.add(action);
	}
	public void drawAction(GL gl)
	{
		if(index < a.size())
		{
			//a.get(index).drawAction(gl);
			a.get(a.size()-1).drawAction(gl);
		}
		/*for(int i = a.size()-1; i >= index; i--)
		{
			a.get(i).drawAction(gl);
		}*/
	}
	public boolean performAction(double tdiff)
	{
		boolean done = false;
		done = a.get(index).performAction(tdiff);
		if(done)
		{
			index++;
			if(index < a.size())
			{
				a.get(index).startAction();
			}
		}
		return index == a.size();
	}
	/**
	 * cancels all the actions in the list
	 */
	public void cancelAction()
	{
		for(int i = a.size()-1; i >= 0; i--)
		{
			a.get(i).cancelAction();
		}
	}
	public void startAction()
	{
		a.get(0).startAction();
	}
	public String toString()
	{
		if(index < a.size())
		{
			return getName()+", "+a.get(index);
		}
		return getName()+", done";
	}
}
