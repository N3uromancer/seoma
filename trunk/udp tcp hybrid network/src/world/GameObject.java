package world;

import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.Operation;

/**
 * represents an object in the world
 * @author Jack
 *
 */
public final class GameObject
{
	private double[] l;
	private boolean isGhost;
	long id;
	int count = 0; //for determining which packets are outdated
	
	/**
	 * creates a new game object
	 * @param x the x coordinate of the objects position
	 * @param y the y coordinate of the objects position
	 * @param isGhost true if this is a ghost object, false if it is
	 * proprietary to the client's world
	 * @param id the id assigned to the object
	 */
	public GameObject(double x, double y, boolean isGhost, long id)
	{
		l = new double[]{x, y};
		this.isGhost = isGhost;
		this.id = id;
	}
	public void translate(double[] t, double tdiff)
	{
		l[0]+=t[0]*tdiff;
		l[1]+=t[1]*tdiff;
	}
	public void setLocation(double x, double y)
	{
		l = new double[]{x, y};
	}
	public void drawGameObject(Graphics2D g)
	{
		int r = 15;
		g.fillOval((int)(l[0]-r), (int)(l[1]-r), 2*r, 2*r);
	}
	/**
	 * gets the id associated with this game object
	 * @return returns the id of this game object
	 */
	public long getID()
	{
		return id;
	}
	public double[] getLocation()
	{
		return l;
	}
	public byte[] getState()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			DataOutputStream dos = new DataOutputStream(baos);
			for(int i = 0; i < 20; i++)
			{
				dos.writeByte(Operation.objectPosUpdate);
				dos.writeLong(id);
				dos.writeInt(count);
				count++;
				dos.writeInt((int)l[0]);
				dos.writeInt((int)l[1]);
			}
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
}
