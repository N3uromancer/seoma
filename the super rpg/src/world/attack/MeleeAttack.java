package world.attack;

import geom.Rectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Iterator;

import world.World;
import world.item.weapon.Weapon;
import world.modifier.Drawable;
import world.networkUpdateable.NetworkUpdateable;
import world.unit.Unit;
import world.unit.attribute.Attribute;

public class MeleeAttack extends NetworkUpdateable implements Drawable
{
	private Unit ownerUnit;
	private Weapon w; //get weapon from unit
	private byte direction;
	private double duration;
	private short id;
	
	public MeleeAttack(boolean isGhost, short id, Unit u, byte direction)
	{
		super(isGhost, id, 0, false);
		
		this.ownerUnit = u;
		this.direction = direction;
		this.duration = 1.0;
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
		g.fillRect((int)ownerUnit.getLocation()[0]-40, (int)ownerUnit.getLocation()[1]-40, 80, 80);
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
		return new Rectangle(ownerUnit.getLocation()[0]-40, ownerUnit.getLocation()[1]-40, 80, 80);
	}
}
