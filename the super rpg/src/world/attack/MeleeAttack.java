package world.attack;

import geom.Rectangle;

import java.awt.Graphics2D;

import world.World;
import world.item.weapon.Weapon;
import world.modifier.Drawable;
import world.networkUpdateable.NetworkUpdateable;
import world.unit.Unit;

public class MeleeAttack extends NetworkUpdateable implements Drawable
{
	private Unit u;
	private Weapon w;
	private byte direction;
	
	public MeleeAttack(boolean isGhost, short id, Unit u, Weapon w, byte direction)
	{
		super(isGhost, id, 0, false);
		
		this.u = u;
		this.w = w;
		this.direction = direction;
		setReady();
	}
	public byte[] getState()
	{
		return null;
	}
	public void loadState(byte[] b)
	{
		
	}
	public void simulate(World w, double tdiff)
	{
		//swing sword here
	}
	public void update(World w, double tdiff)
	{
		//swing sword here
		//damage hit units here
	}
	public boolean isRelevant(short id, World w)
	{
		return false;
	}
	public void draw(Graphics2D g)
	{
		
	}
	public boolean isDisplayed()
	{
		return true;
	}
	public Rectangle getBounds()
	{
		return null;
	}
}
