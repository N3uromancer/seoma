package ui.userIO.userInput;

public abstract class MouseInput implements UserInput
{
	private int x, y;
	private boolean rightClick;
	
	/**
	 * the location that the mouse click occured at in the opengl coordinate system
	 * @param x the x location of the click in the opengl coordinate system
	 * @param y the y location of the click in the opengl coordinate system
	 */
	public MouseInput(int x, int y, boolean rightClick)
	{
		this.x = x;
		this.y = y;
		this.rightClick = rightClick;
	}
	public int[] getLocation()
	{
		return new int[]{x, y};
	}
	public boolean isRightClick()
	{
		return rightClick;
	}
}
