package gameEngine;

import gameEngine.world.World;
import ui.userIO.userInput.UserInput;

public class GameEngine
{
	World w;
	public void updateGameEngine(UserInput[] ui, double tdiff)
	{
		w.updateWorld(ui, tdiff);
	}
}
