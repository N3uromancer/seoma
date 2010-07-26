package world.attack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Iterator;

import geom.Rectangle;

import world.World;
import world.item.weapon.Weapon;
import world.modifier.Drawable;
import world.networkUpdateable.NetworkUpdateable;
import world.unit.Unit;
import world.unit.attribute.Attribute;

public class MeleeAttack extends NetworkUpdateable implements Drawable
{
	private Unit ownerUnit;
	private Weapon w;
	private byte direction;
	private double duration;
	private double x;
	private double y;
	private short id;
	
	public MeleeAttack(boolean isGhost, short id, Unit u, Weapon w, byte direction)
	{
		super(isGhost, id, 0, false);
		
		this.ownerUnit = u;
		this.w = w;
		this.direction = direction;
		this.duration = 1.0;
		this.x = u.getLocation()[0];
		this.y = u.getLocation()[1];
		this.id = id;
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
		duration -= tdiff;
		if (duration <= 0.0)
			setDead();
	}
	public void update(World w, double tdiff)
	{
		simulate(w, tdiff);
		//damage hit units here
		HashSet hitUnits = new HashSet<Unit>();
		hitUnits = w.getAssociatedRegion(id).getIntersectedUnits(this);
		Iterator<Unit> i = hitUnits.iterator();
		while(i.hasNext())
		{
			Unit u = i.next();
			if(!u.equals(ownerUnit))
				u.getAttributeManager().setAttribute(Attribute.health, 0);
		}
	}
	public void draw(Graphics2D g)
	{
		g.setColor(Color.WHITE);
		g.fillRect((int)x-40, (int)y-40, 80, 80);
	}
	public boolean isDisplayed()
	{
		if (duration <= 0.0)
			return false;
		return true;
	}
	@Override
	public boolean isRelevant(short id, World w) 
	{
		// TODO Auto-generated method stub
		return false;
	}
	public Rectangle getBounds()
	{
		return new Rectangle(x-40, y-40, 80, 80);
	}
}
