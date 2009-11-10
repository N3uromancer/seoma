package gameEngine.world.terrain;

import gameEngine.world.Destructable;
import gameEngine.world.World;
import utilities.Polygon;
import utilities.SpatialPartition;

/**
 * handles all terrain features as well as weather effects
 * @author Jack
 *
 */
public class TerrainEngine
{
	SpatialPartition it; //indestructable terrain partition
	SpatialPartition dt; //desctructable terrain partition
	
	public TerrainEngine(World w)
	{
		it = new SpatialPartition(0, 0, w.getWidth(), w.getHeight(), 20, 75, 200);
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
	public void registerTerrain(Terrain t)
	{
		if(isDestructable(t))
		{
			dt.addRegion(t);
		}
	}
	private boolean isDestructable(Terrain t)
	{
		for(int i = 0; i < t.getClass().getInterfaces().length; i++)
		{
			if(t.getClass().getInterfaces()[i] == Destructable.class)
			{
				return true;
			}
		}
		return false;
	}
}
