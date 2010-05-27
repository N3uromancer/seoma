package world.controller;

import java.util.HashMap;
import java.util.HashSet;

public final class UserInput
{
	private HashSet<Character> down;
	private HashMap<short[], Boolean> mousePresses;
	
	/**
	 * creates a new user input representation
	 * @param down the keys currently depressed by the user
	 * @param mousePresses the mouse clicks of the user, values in the map
	 * are true if the mouse click was a right click
	 */
	public UserInput(HashSet<Character> down, HashMap<short[], Boolean> mousePresses)
	{
		this.down = down;
		this.mousePresses = mousePresses;
	}
	public HashMap<short[], Boolean> getMousePresses()
	{
		return mousePresses;
	}
	public HashSet<Character> getKeyPresses()
	{
		return down;
	}
}
