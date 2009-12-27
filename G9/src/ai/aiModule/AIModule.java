package ai.aiModule;

import gameEngine.world.World;

import java.util.ArrayList;
import java.util.HashMap;

import ai.AI;

import ui.userIO.userInput.UserInput;

/**
 * something that can be added to an ai in order to carry out
 * an action, ai utilities are meant to be used as preassembled
 * pieces to build an ai
 * 
 * @author Jack
 *
 */
public interface AIModule
{
	/**
	 * called to update the module
	 * @param ai the ai the module is attached to
	 * @param w a reference to the game world
	 * @param ui a list representing user input for the passed ai
	 * @param tdiff the time differential
	 */
	public void updateModule(AI ai, World w, HashMap<Class<? extends UserInput>, ArrayList<UserInput>> ui, double tdiff);
}
