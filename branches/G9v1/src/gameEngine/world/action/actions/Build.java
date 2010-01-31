package gameEngine.world.action.actions;

import javax.media.opengl.GL;


import gameEngine.world.World;
import gameEngine.world.action.Action;
import gameEngine.world.unit.BuildOrder;

public class Build extends Action
{
	BuildOrder bo;
	World w;
	
	/**
	 * creates a new build action
	 * @param bo a reference to the build order that is related
	 * to this build action
	 */
	public Build(BuildOrder bo, World w)
	{
		super("build");
		this.bo = bo;
		this.w = w;
	}
	public void cancelAction()
	{
		bo.cancel();
	}
	public void drawAction(GL gl)
	{
		
	}
	public boolean performAction(double tdiff)
	{
		return bo.isComplete();
	}
	public void startAction()
	{
		w.getUnitEngine().registerBuildOrder(bo);
		//System.out.println("build action started, build order registered");
	}
}
