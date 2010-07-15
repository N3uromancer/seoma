package board;

public final class Move
{
	private byte start;
	private byte dest;
	
	public Move(byte start, byte dest)
	{
		this.start = start;
		this.dest = dest;
	}
	public byte getStart()
	{
		return start;
	}
	public byte getDest()
	{
		return dest;
	}
}
