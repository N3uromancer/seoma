package world.unit;

import geom.Rectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import world.World;
import world.action.AttackAction;
import world.item.Inventory;
import world.modifier.Drawable;
import world.modifier.ObjectType;
import world.networkUpdateable.NetworkUpdateable;
import world.unit.attribute.Attribute;
import world.unit.attribute.AttributeManager;

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
	private short r; //radius
	private byte unitType; //the type of unit
	private AttributeManager am = new AttributeManager();
	private Inventory inventory = new Inventory();
	
	double m = 100; //movement speed

	/**
	 * blank constructor for creating the unit on clients, state information
	 * is instead loaded as it is received from the server
	 * @param isGhost
	 */
	public Unit(boolean isGhost, short id, byte unitType, short r)
	{
		super(isGhost, id, ObjectType.unit, 1, true);
		this.r = r;
		this.unitType = unitType;
		initializeActions();
	}
	/**
	 * constructor for creating the unit and specifying its state, used for creating
	 * units on the server, units created in this manner are declared to be ready for use
	 * @param isGhost
	 * @param l
	 * @param r radius of the unit
	 */
	public Unit(boolean isGhost, short id, byte unitType, double[] l, short r)
	{
		super(isGhost, id, ObjectType.unit, 1, true);
		this.l = l;
		this.r = r;
		this.unitType = unitType;
		am.setAttribute(Attribute.health, 100);
		initializeActions();
		setReady();
	}
	public Inventory getInventory()
	{
		return inventory;
	}
	/**
	 * initializes all the actions a unit can perform
	 */
	private void initializeActions()
	{
		registerAction(new AttackAction(this));
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
		if(!(this instanceof Avatar))
		{
			g.setColor(Color.blue);
			//System.out.println("l=("+l[0]+", "+l[1]+"), r="+r);
		}
		g.fillOval((int)(l[0]-r), (int)(l[1]-r), r*2, r*2);
		//g.setColor(Color.black);
		//g.drawOval((int)(l[0]-r), (int)(l[1]-r), r*2, r*2);
	}
	public void update(World w, double tdiff)
	{
		//System.out.println(getID()+" updated");
		l[0]+=m*tdiff;
		if(l[0] > 500 || l[0] < 0)
		{
			m *= -1;
			//setDead();
		}
	}
	public void simulate(World w, double tdiff)
	{
		//System.out.println(getID()+" simulated");
	}
	public boolean isDisplayed()
	{
		return true;
	}
	public Rectangle getBounds()
	{
		return new Rectangle(l[0]-r, l[1]-r, r*2, r*2);
	}
	public void loadInitialState(byte[] b)
	{
		
	}
	public byte[] getInitialState()
	{
		return new byte[]{unitType};
	}
}
