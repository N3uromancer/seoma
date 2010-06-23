package world.terrain;

import java.awt.Color;

public enum Terrain
{
	wall(Color.black),
	grass(Color.green);
	
	private final Color c;
	Terrain(Color c)
	{
		this.c = c;
	}
	public Color getColor()
	{
		return c;
	}
}
