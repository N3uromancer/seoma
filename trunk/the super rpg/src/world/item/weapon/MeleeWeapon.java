package world.item.weapon;

import geom.LineSegment;
import world.World;

/**
 * defines a weapon that has defined bounds and swings to damage targets
 * @author Jack
 *
 */
public class MeleeWeapon extends Weapon
{
	private LineSegment[] bounds;
	private double swingLength; //length of the swing in degrees
	private double swingSpeed; //speed of swing in degrees/sec 
	
	public MeleeWeapon(LineSegment[] bounds, double swingLength, double swingSpeed)
	{
		this.bounds = bounds;
		this.swingLength = swingLength;
		this.swingSpeed = swingSpeed;
	}
	public LineSegment[] getBounds()
	{
		return bounds;
	}
	public double getSwingLength()
	{
		return swingLength;
	}
	public double getSwingSpeed()
	{
		return swingSpeed;
	}
	public void executeAttack(World w, byte[] data)
	{
		//code goes here to generate weapon swing (possible register an updateable with the world)
	}
}
