package ai.humanAI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import javax.media.opengl.GL;
import ai.AI;
import ai.humanAI.buildUI.BuildUI;
import ai.humanAI.userCommand.*;
import ui.userIO.userInput.*;
import utilities.Camera;
import utilities.region.Region;
import gameEngine.world.World;
import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Unit;

/**
 * command structure:
 * -interpreted user actions set the user command variable
 * -all units are ordered each iteration of the performAIFunctions method to follow the current user command
 * -the user command varaible is nulled each iteration after the orders are given
 * @author Jack
 *
 */
public class HumanAI extends AI
{
	UserCommand[] uc = new UserCommand[10]; //command buffer
	int ucindex = 0;
	/**
	 * the number of selected units
	 */
	int selected = 0;
	
	/**
	 * if true then the action the unit is currently carrying out is overriden,
	 * all queued actions are deleted, the user command just issued replaces all
	 * other actions the unit was doing beforehand
	 */
	boolean override = false;
	
	double sx; //the starting x coord of the mouse when it began dragging
	double sy;
	boolean dragging = false; //true if the mouse is currently being dragged
	
	double mousex = 0; //x location of the mouse
	double mousey = 0;
	
	boolean builderSelected = false; //true if a unit that can build other units is selected
	
	public HumanAI(Owner o)
	{
		super(o);
	}
	public Camera getCamera()
	{
		return null;
	}
	public void drawUI(GL gl)
	{
		if(dragging)
		{
			//System.out.println("here");
			double d = .99; //depth
			gl.glColor4d(1, .5, 0, .3);
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3d(sx, sy, d);
			gl.glVertex3d(mousex, sy, d);
			gl.glVertex3d(mousex, mousey, d);
			gl.glVertex3d(sx, mousey, d);
			gl.glEnd();
			d+=.01;
			gl.glLineWidth(4);
			gl.glColor4d(1, 1, 0, .95);
			gl.glColor4d(1, .5, 0, .3);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3d(sx, sy, d);
			gl.glVertex3d(mousex, sy, d);
			gl.glVertex3d(mousex, mousey, d);
			gl.glVertex3d(sx, mousey, d);
			gl.glEnd();
		}
		if(selected == 1)
		{
			new BuildUI().drawBuildUI(gl, 30, 300, 100);
		}
	}
	/**
	 * cycles through all passed user input and properly registers it
	 * with the ai
	 * @param ui the user input to be registered
	 */
	private void registerUserInput(ArrayList<UserInput> ui, World w)
	{
		for(int i = 0; i < ui.size(); i++)
		{
			UserInput input = ui.get(i);
			if(input instanceof MousePress)
			{
				MousePress m = (MousePress)input;
				interpretMousePress(m.getLocation()[0], m.getLocation()[1], m.isRightClick(), w);
			}
			else if(input instanceof MouseRelease)
			{
				MouseRelease m = (MouseRelease)input;
				interpretMouseRelease(m.getLocation()[0], m.getLocation()[1], m.isRightClick(), w);
			}
			else if(input instanceof MouseClick)
			{
				MouseClick m = (MouseClick)input;
				interpretMouseClick(m.getLocation()[0], m.getLocation()[1], m.isRightClick(), w);
			}
			else if(input instanceof MouseMove)
			{
				MouseMove m = (MouseMove)input;
				interpretMouseMotion(m.getLocation()[0], m.getLocation()[1], false, m.isRightClick());
			}
			else if(input instanceof MouseDrag)
			{
				MouseDrag m = (MouseDrag)input;
				interpretMouseMotion(m.getLocation()[0], m.getLocation()[1], true, m.isRightClick());
			}
		}
	}
	public void performAIFunctions(World w, ArrayList<UserInput> ui)
	{
		if(ui != null)
		{
			//System.out.println("registering input");
			registerUserInput(ui, w);
		}
		
		LinkedList<Unit> u = getUnits(w);
		Iterator<Unit> i = u.iterator();
		while(i.hasNext())
		{
			Unit unit = i.next();
			
			/*
			 * cycles through the buffer of user commands and executes them on each unit
			 */
			boolean endReached = false; //end of the buffer reached
			for(int q = 0; q < uc.length && !endReached; q++)
			{
				if(uc[q] != null)
				{
					uc[q].updateUnit(unit, this, override, w);
				}
				else
				{
					endReached = true;
				}
			}
		}
		uc = new UserCommand[uc.length];
		ucindex = 0;
		override = false;
	}
	private void interpretMousePress(double x, double y, boolean rightClick, World w)
	{
		if(!rightClick)
		{
			if(!selectUnit(x, y, w))
			{
				registerUserCommand(new MoveCommand(x, y));
				override = true;
			}
		}
		else
		{
			//uc = new DeselectCommand();
			registerUserCommand(new DeselectCommand());
		}
	}
	private void interpretMouseRelease(double x, double y, boolean rightClick, World w)
	{
		if(dragging)
		{
			registerUserCommand(new DeselectCommand());
			registerUserCommand(new DragSelectCommand(sx, sy, x, y));
			dragging = false;
		}
		else if(!rightClick && !selectUnit(x, y, w))
		{
			registerUserCommand(new MoveCommand(x, y));
			override = true;
		}
	}
	/**
	 * gets the number of selected units
	 * @return
	 */
	public int getSelectedUnits()
	{
		return selected;
	}
	/**
	 * sets the number of selected units
	 * @param setter
	 */
	public void setSelectedUnits(int setter)
	{
		selected = setter;
	}
	private void interpretMouseClick(double x, double y, boolean rightClick, World w)
	{
		
	}
	/**
	 * adds the passed user command to the list of user commands
	 * to be executed if there is space in the command list buffer
	 * @param userCommand
	 */
	private void registerUserCommand(UserCommand userCommand)
	{
		if(ucindex < uc.length)
		{
			uc[ucindex] = userCommand;
			ucindex++;
		}
	}
	/**
	 * tests units that this ai controls to see if they are selected by the mouse press
	 * @param x the x coordinate of the mouse press
	 * @param y the y coordinate of the mouse press
	 * @return
	 */
	private boolean selectUnit(double x, double y, World w)
	{
		HashSet<Region> u = w.getUnitEngine().getUnitPartition(o).getIntersections(x, y, 1, 1);
		Iterator<Region> i = u.iterator();
		boolean unitClicked = false;
		while(i.hasNext() && !unitClicked)
		{
			Unit unit = (Unit)i.next();
			unitClicked = true;
			unit.setSelected(true);
			selected++;
		}
		return unitClicked;
	}
	private void interpretMouseMotion(double x, double y, boolean dragged, boolean rightClick)
	{
		if(!dragging && dragged && !rightClick)
		{
			//System.out.println("dragging mouse registered in human ai");
			dragging = true;
			sx = x;
			sy = y;
		}
		mousex = x;
		mousey = y;
	}
}
