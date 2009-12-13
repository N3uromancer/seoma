package ui.userIO.userInput;

public abstract class KeyInput implements UserInput
{
	private char c;
	
	public KeyInput(char c)
	{
		this.c = c;
	}
	public char getCharacter()
	{
		return c;
	}
}
