package gameEngine.ai.humanAI;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import javax.media.opengl.GL;

import ai.AI;
import utilities.Region;
import gameEngine.ai.humanAI.userCommand.*;
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
public class BasicHumanAI2 extends AI
{
	UserCommand[] uc = new UserCommand[10]; //command buffer
	int ucindex = 0;
	
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
	
	public BasicHumanAI2(Owner o, World w)
	{
		super(o, w);
	}
	public void drawUI(GL gl)
	{
		if(dragging)
		{
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
	}
	public void performAIFunctions()
	{
		//System.out.println("here");
		LinkedList<Unit> u = getUnits();
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
					uc[q].updateUnit(unit, this, override);
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
	public void interpretMouseClick(double x, double y, boolean pressed, boolean rightClick)
	{
		if(pressed)
		{
			if(!rightClick)
			{
				if(!selectUnit(x, y))
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
		else
		{
			if(dragging)
			{
				registerUserCommand(new DeselectCommand());
				registerUserCommand(new DragSelectCommand(sx, sy, x, y));
				dragging = false;
			}
			else if(!rightClick && !selectUnit(x, y))
			{
				registerUserCommand(new MoveCommand(x, y));
				override = true;
			}
		}
	}
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
	private boolean selectUnit(double x, double y)
	{
		HashSet<Region> u = w.getUnitEngine().getUnitPartition(o).getIntersections(new Region(x, y, 1, 1));
		Iterator<Region> i = u.iterator();
		boolean unitClicked = false;
		while(i.hasNext() && !unitClicked)
		{
			Unit unit = (Unit)i.next();
			if(unit.getOwner() == o)
			{
				unitClicked = true;
				unit.setSelected(true);
			}
		}
		return unitClicked;
	}
	public void interpretMouseMove(double x, double y, boolean dragged, boolean rightClick)
	{
		if(!dragging && dragged && !rightClick)
		{
			dragging = true;
			sx = x;
			sy = y;
		}
		mousex = x;
		mousey = y;
	}
}
