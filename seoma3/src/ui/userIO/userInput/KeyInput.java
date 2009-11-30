package ui.userIO.userInput;

public abstract class KeyInput extends UserInput
{
	private char c;
	
	public KeyInput(byte owner, char c)
	{
		super(owner);
	}
	public char getCharacter()
	{
		return c;
	}
}
