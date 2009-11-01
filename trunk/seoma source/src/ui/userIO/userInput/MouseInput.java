package ui.userIO.userInput;

public abstract class MouseInput extends UserInput
{
	private int x, y;
	private boolean rightClick;
	
	/**
	 * the location that the mouse click occured at in the opengl coordinate system
	 * @param x
	 * @param y
	 */
	public MouseInput(byte owner, int x, int y, boolean rightClick)
	{
		super(owner);
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
