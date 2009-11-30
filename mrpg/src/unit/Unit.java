package unit;

import java.util.ArrayList;
import java.util.HashMap;
import javax.media.opengl.GL;
import ai.AI;
import ai.UserAI;
import ui.userIO.userInput.UserInput;
import updateManager.Drawable;
import updateManager.UpdateManager;
import updateManager.Updateable;
import utilities.region.RectRegion;

public class Unit extends RectRegion implements Updateable, Drawable
{
	double movement;
	AI ai; //the ai managing this unit
	
	double maxLife = 100;
	double life;
	
	byte owner;
	
	public Unit(byte owner, double x, double y, double width, double height, double movement)
	{
		super(x, y, width, height);
		this.owner = owner;
		this.movement = movement;
		
		life = maxLife;
	}
	public void setAI(AI ai)
	{
		this.ai = ai;
	}
	public byte getOwner()
	{
		return owner;
	}
	/**
	 * returns how far this unit can move
	 * @return returns how far this unit can move
	 */
	public double getMovement()
	{
		return movement;
	}
	/**
	 * returns the ai managing this unit
	 * @return
	 */
	public AI getAI()
	{
		return ai;
	}
	public void update(UpdateManager um, double tdiff, HashMap<Byte, ArrayList<UserInput>> ui)
	{
		if(ai != null)
		{
			if(ai instanceof UserAI && ui.get(owner) != null)
			{
				UserAI uai = (UserAI)ai;
				uai.interpretUserInput(ui.get(owner));
			}
			ai.performCommands(tdiff);
		}
	}
	public boolean isDead()
	{
		return life <= 0;
	}
	public void draw(GL gl)
	{
		gl.glColor3d(1, 0, 0);
		fillRegion(gl, 1);
	}
}
