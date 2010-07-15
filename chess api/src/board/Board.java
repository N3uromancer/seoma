package board;

public interface Board
{
	public Move[] getLegalMoves();
	public void movePiece(Move m);
}
