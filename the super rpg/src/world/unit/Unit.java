package world.unit;

import geom.Rectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import world.RelevantSet;
import world.World;
import world.item.Inventory;
import world.modifier.Drawable;
import world.networkUpdateable.NetworkUpdateable;
import world.unit.attribute.Attribute;
import world.unit.attribute.AttributeManager;
import world.unit.script.Script;

/**
 * defines a unit, units are capable of interacting with the game world
 * by executing actions
 * @author Jack
 *
 */
public class Unit extends NetworkUpdateable implements Drawable
{
	/**
	 * the location of the center of the unit
	 */
	private double[] l = new double[2]; //stored as a double but written over network as a short
	private short radius; //radius
	private AttributeManager am = new AttributeManager();
	private Inventory inventory = new Inventory();
	private Script script;

	/**
	 * blank constructor for creating the unit on clients, state information
	 * is instead loaded as it is received from the server
	 * @param isGhost
	 */
	public Unit(boolean isGhost, short id, short radius)
	{
		super(isGhost, id, 1, true);
		this.radius = radius;
		
		am.setAttribute(Attribute.health, 100);
		am.setAttribute(Attribute.movement, 100);
	}
	/**
	 * constructor for creating the unit and specifying its state, used for creating
	 * units on the server, units created in this manner are declared to be ready for use
	 * @param isGhost
	 * @param l
	 * @param radius radius of the unit
	 */
	public Unit(boolean isGhost, short id, double[] l, short radius)
	{
		super(isGhost, id, 1, true);
		this.l = l;
		this.radius = radius;
		
		am.setAttribute(Attribute.health, 100);
		am.setAttribute(Attribute.movement, 100);
		
		setReady();
	}
	public AttributeManager getAttributeManager()
	{
		return am;
	}
	public short getRadius()
	{
		return radius;
	}
	public Inventory getInventory()
	{
		return inventory;
	}
	/**
	 * gets the location of the center of the unit
	 * @return
	 */
	public double[] getLocation()
	{
		return new double[]{l[0], l[1]};
	}
	/**
	 * sets the location of the center of the unit
	 * @param l
	 */
	public void setLocation(double[] l)
	{
		this.l = l;
	}
	public byte[] getState()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.writeShort((short)l[0]);
			dos.writeShort((short)l[1]);
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
	public void loadState(byte[] b)
	{
		//System.out.println("id="+getID()+" state information loaded");
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		DataInputStream dis = new DataInputStream(bais);
		try
		{
			l[0] = dis.readShort();
			l[1] = dis.readShort();
		}
		catch(IOException e){}
		setReady();
	}
	public void draw(Graphics2D g)
	{
		g.setColor(Color.red);
		g.fillOval((int)(l[0]-radius), (int)(l[1]-radius), radius*2, radius*2);
		//g.setColor(Color.black);
		//g.drawOval((int)(l[0]-r), (int)(l[1]-r), r*2, r*2);
	}
	public void update(World w, double tdiff)
	{
		if(script != null)
		{
			script.updateScript(w, tdiff);
		}
		if(am.getAttribute(Attribute.health) <= 0)
		{
			//System.out.println("unit "+getID()+" killed");
			setDead();
		}
	}
	public void simulate(World w, double tdiff)
	{
		//System.out.println(getID()+" simulated");
		//intersects = w.getAssociatedRegion(getID()).getIntersectedUnits(this).size() > 0;
	}
	public void setScript(Script s)
	{
		script = s;
	}
	public boolean isDisplayed()
	{
		return true;
	}
	public Rectangle getBounds()
	{
		return new Rectangle(l[0]-radius, l[1]-radius, radius*2, radius*2);
	}
	public boolean isRelevant(short id, RelevantSet set, World w)
	{
		return w.getAssociatedRegion(getID()) == w.getAssociatedRegion(id);
	}
}
