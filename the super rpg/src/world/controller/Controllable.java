package world.controller;

import display.Camera;

/**
 * defines an interface for game objects that can be controlled through user input
 * @author Jack
 *
 */
public interface Controllable
{
	public void interpretUserInput(UserInput ui, double tdiff);
	public void adjustCamera(Camera c);
}
