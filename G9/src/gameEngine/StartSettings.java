package gameEngine;

import gameEngine.world.owner.Owner;

/**
 * the settings used to start the game
 * @author Jack
 *
 */
public class StartSettings
{
	int mapWidth;
	int mapHeight;
	Owner[] owners;
	String[] startingUnits;
	
	public StartSettings(int mapWidth, int mapHeight, Owner[] owners, String[] startingUnits)
	{
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.owners = owners;
		this.startingUnits = startingUnits;
	}
	public int getMapWidth()
	{
		return mapWidth;
	}
	public int getMapHeight()
	{
		return mapHeight;
	}
	public Owner[] getOwners()
	{
		return owners;
	}
	public String[] getStartingUnits()
	{
		return startingUnits;
	}
}
