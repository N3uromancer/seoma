package world.unit;

import geom.Rectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import world.GameObject;
import world.World;
import world.modifier.Drawable;
import world.modifier.NetworkUpdateable;
import world.modifier.Updateable;
import world.unit.attribute.Attribute;
import world.unit.attribute.AttributeManager;

public class Unit extends GameObject implements Updateable, NetworkUpdateable, Drawable
{
	private short id;
	/**
	 * the location of the center of the unit
	 */
	private double[] l; //stored as a double but written over network as a short
	private short r;
	private AttributeManager am = new AttributeManager();
	private boolean ready = false;

	/**
	 * blank constructor for creating the unit on clients, state information
	 * is instead loaded as it is received from the server
	 * @param isGhost
	 */
	public Unit(boolean isGhost)
	{
		super(isGhost);
	}
	/**
	 * constructor for creating the unit and specifying its state, used for creating
	 * units on the server, units created in this manner are declared to be ready for use
	 * @param isGhost
	 * @param l
	 * @param r radius of the unit
	 */
	public Unit(boolean isGhost, double[] l, short r)
	{
		super(isGhost);
		this.l = l;
		this.r = r;
		ready = true;
		
		am.setAttribute(Attribute.health, 100);
	}
	public boolean isReady()
	{
		return ready;
	}
	/**
	 * gets the location of the center of the unit
	 * @return
	 */
	public double[] getLocation()
	{
		return l;
	}
	/**
	 * sets the location of the center of the unit
	 * @param l
	 */
	public void setLocation(double[] l)
	{
		this.l = l;
	}
	public short getID()
	{
		return id;
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
	public int getUpdatePriority()
	{
		return 0;
	}
	public void loadState(byte[] b)
	{
		ready = true;
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		DataInputStream dis = new DataInputStream(bais);
		try
		{
			l[0] = dis.readShort();
			l[1] = dis.readShort();
		}
		catch(IOException e){}
	}
	public void setID(short id)
	{
		this.id = id;
	}
	public void draw(Graphics2D g)
	{
		g.setColor(Color.red);
		g.fillOval((int)(l[0]-r), (int)(l[1]-r), r*2, r*2);
	}
	public void update(World w, double tdiff)
	{
		
	}
	public boolean isDisplayed()
	{
		return true;
	}
	public Rectangle getBounds()
	{
		return new Rectangle(l[0]-r, l[1]-r, r*2, r*2);
	}
}
