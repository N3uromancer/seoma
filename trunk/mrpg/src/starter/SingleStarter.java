package starter;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

import ai.UserAI;

import display.Display;

import gameEngine.GameEngine;
import terrain.TerrainEngine;
import ui.UIFrame;
import unit.Unit;
import unit.UnitEngine;
import updateManager.Updateable;
import world.World;

/**
 * starts a single player game
 * @author Jack
 *
 */
public class SingleStarter
{
	public static void main(String[] args)
	{
		World world = new World();
		UnitEngine ue = new UnitEngine(300, 300);
		world.registerUpdater(ue);
		world.registerUpdater(new TerrainEngine());
		
		GLCanvas c = new GLCanvas(new GLCapabilities());
		GameEngine ge = new GameEngine(false, world, c);
		UIFrame uif = new UIFrame(ge, c, new Display(world));
		uif.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Unit u = new Unit(ge.getOwner(), 100, 100, 50, 50, 30);
		u.setAI(new UserAI(u));
		ue.register((Updateable)u);
	}
}
