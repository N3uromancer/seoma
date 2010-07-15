package piece;

public enum Piece
{
	blackPawn(new byte[]{8}),
	whitePawn(new byte[]{-8}),
	knight,
	queen,
	king,
	bishop,
	rook
	
	private byte[] moves;
	Piece(byte[] moves)
	{
		this.moves = moves;
	}
	public byte[] getMoves()
	{
		return moves;
	}
}
