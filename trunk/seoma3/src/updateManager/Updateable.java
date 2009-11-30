package updateManager;

import java.util.ArrayList;
import java.util.HashMap;

import ui.userIO.userInput.UserInput;

public interface Updateable extends Element
{
	public void update(UpdateManager um, double tdiff, HashMap<Byte, ArrayList<UserInput>> ui);
}
