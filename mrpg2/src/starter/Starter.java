package starter;

import display.Display;
import display.screen.HomeScreen;

/**
 * starts the game
 * @author Jack
 *
 */
public class Starter
{
	public static void main(String[] args)
	{
		Display d = new Display();
		d.loadScreen(new HomeScreen(d));
		d.updateDisplay();
	}
}
