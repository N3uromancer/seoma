package world.controller;

import world.networkUpdateable.NetworkUpdateable;
import display.Camera;

/**
 * defines an interface for game objects that can be controlled through user input
 * @author Jack
 *
 */
public interface Controllable
{
	/**
	 * interprets the user input from the controller
	 * @param n a thread safe reference to the object being controlled
	 * @param ui
	 * @param tdiff
	 */
	public void interpretUserInput(NetworkUpdateable n, UserInput ui, double tdiff);
	public void adjustCamera(Camera c);
	/**
	 * gets the id of the object being controlled so that
	 * the world can get a safe reference to the object
	 * @return returns the id of the object being controlled
	 */
	public short getControlledObjID();
}
