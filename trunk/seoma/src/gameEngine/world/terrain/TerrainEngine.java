package gameEngine.world.terrain;

import gameEngine.world.World;
import utilities.Polygon;
import utilities.SpatialPartition;

public class TerrainEngine
{
	SpatialPartition t; //terrain partition
	
	public TerrainEngine(World w)
	{
		t = new SpatialPartition(0, 0, w.getWidth(), w.getHeight(), 20, 75, 200);
	}
	/**
	 * tests to see if the passed polygon intersects any terrain
	 * features of the world
	 * @param p the polygon to be tested
	 * @return returns true if the passed polygon intersects a terrain
	 * feature, false otherwise
	 */
	public boolean intersects(Polygon p)
	{
		return false;
	}
}
