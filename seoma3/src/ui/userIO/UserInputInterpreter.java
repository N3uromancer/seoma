package ui.userIO;

public interface UserInputInterpreter
{
	public abstract void keyAction(char c, boolean pressed);
	public abstract void keyTyped(char c);
	/**
	 * method is called every time a mouse press or release occurs
	 * @param x
	 * @param y the y position of the mouse click in game space (not the java coord system)
	 * @param pressed
	 * @param rightClick true if the mouse was clicked with a right click
	 */
	public abstract void mouseAction(int x, int y, boolean pressed, boolean rightClick);
	public abstract void mouseClickAction(int x, int y, boolean rightClick);
	public abstract void mouseMoveAction(int x, int y, boolean dragged, boolean rightClick);
}
