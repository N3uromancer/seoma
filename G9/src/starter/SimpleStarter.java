package starter;

import java.util.HashMap;
import ai.AI;
import ai.computerAI.*;
import gameEngine.world.owner.Owner;

public class SimpleStarter
{
	public static void main(String[] args)
	{
		double[] c1 = {1, 0, 0};
		double[] c2 = {0, 0, 1};
		double[] c3 = {1, 0, 1};
		double[] c4 = {1, 1, 0};
		final Owner[] owners = {new Owner((byte)0, "player 1", c1), new Owner((byte)1, "player 2", c2)};
		/*final Owner[] owners = {new Owner((byte)0, "player 1", c1), new Owner((byte)1, "player 2", c2),
				new Owner((byte)2, "player 3", c3), new Owner((byte)3, "player 4", c4)};*/

		HashMap<Owner, AI> ais = new HashMap<Owner, AI>();
		ais.put(owners[0], new EjemploAI(owners[0]));
		ais.put(owners[1], new StalinArmy(owners[1]));
		
		Starter.startGameGUI(ais, owners, System.currentTimeMillis());
	}
}
