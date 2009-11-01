package ui.userIO;

public abstract class UserInputInterpreter
{
	public abstract void keyAction(char c, boolean pressed);
	/**
	 * method is called every time a mouse click occurs
	 * @param x
	 * @param y the y position of the mouse click in game space (not the java coord system)
	 * @param pressed
	 * @param rightClick
	 */
	public abstract void mouseAction(int x, int y, boolean pressed, boolean rightClick);
	public abstract void mouseMoveAction(int x, int y, boolean dragged, boolean rightClick);
}
