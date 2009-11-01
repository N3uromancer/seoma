package ui.userIO.userInput;

/**
 * represents user input
 * @author Jack
 *
 */
public abstract class UserInput
{
	private byte owner;
	
	public UserInput(byte owner)
	{
		this.owner = owner;
	}
	public byte getOwner()
	{
		return owner;
	}
}
