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
import world.modifier.Drawable;
import world.modifier.NetworkUpdateable;
import world.unit.attribute.Attribute;
import world.unit.attribute.AttributeManager;

public class Unit extends NetworkUpdateable implements Drawable
{
	/**
	 * the location of the center of the unit
	 */
	private double[] l; //stored as a double but written over network as a short
	private short r;
	private AttributeManager am = new AttributeManager();

	/**
	 * blank constructor for creating the unit on clients, state information
	 * is instead loaded as it is received from the server
	 * @param isGhost
	 */
	public Unit(boolean isGhost, short id, byte type)
	{
		super(isGhost, id, type, 1);
	}
	/**
	 * constructor for creating the unit and specifying its state, used for creating
	 * units on the server, units created in this manner are declared to be ready for use
	 * @param isGhost
	 * @param l
	 * @param r radius of the unit
	 */
	public Unit(boolean isGhost, short id, byte type, double[] l, short r)
	{
		super(isGhost, id, type, 1);
		this.l = l;
		this.r = r;
		am.setAttribute(Attribute.health, 100);
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
		g.fillOval((int)(l[0]-r), (int)(l[1]-r), r*2, r*2);
	}
	public void update(World w, double tdiff)
	{
		
	}
	public void simulate(World w, double tdiff)
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
