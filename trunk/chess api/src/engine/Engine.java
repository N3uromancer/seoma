package engine;

import board.Board;
import board.Move;

public interface Engine
{
	public Move getMove(Board b, boolean white);
}
