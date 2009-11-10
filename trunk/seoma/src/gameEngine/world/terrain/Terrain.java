package gameEngine.world.terrain;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import gameEngine.world.DepthConstants;

import javax.media.opengl.GL;

import utilities.Polygon;
import utilities.region.RectRegion;
import utilities.region.Region;

/**
 * defines a terrain object
 * 
 * a bounded region of space which contains at least one polygon,
 * each terrain object may contain as many sub level terrain objects
 * as necesary, the bounds of a given terrain object contains the polygon
 * of the given terrain object as well as the polygons of all sub level terrain
 * objects
 * @author Jack
 *
 */
public class Terrain implements Region
{
	/**
	 * the sub terrain under this terrain object
	 */
	LinkedList<Terrain> t = new LinkedList<Terrain>();
	Polygon p;
	double[] c; //color
	
	/**
	 * the bounds of this terrain feature
	 */
	RectRegion bounds;
	
	/**
	 * creates a new terrain object
	 * @param p the polygon representing the terrain
	 * @param c the color of the terrain
	 */
	public Terrain(Polygon p, double[] c)
	{
		this.p = p;
		this.c = c;
		determineBounds();
	}
	public void drawTerrain(GL gl)
	{
		gl.glLineWidth(4);
		gl.glColor3d(0, 0, 0);
		p.fillPolygon(gl, DepthConstants.terrain);
		gl.glColor3d(c[0], c[1], c[2]);
		p.drawPolygon(gl, DepthConstants.terrain);
	}
	/**
	 * tests to see if the polygon that represents this terrain feature or those
	 * of any of the sub terrain features contains the passed coordinate
	 */
	public boolean contains(double px, double py)
	{
		if(bounds.contains(px, py))
		{
			if(p.contains(px, py))
			{
				return true;
			}
			Iterator<Terrain> i = t.iterator();
			while(i.hasNext())
			{
				if(i.next().getPolygon().contains(px, py))
				{
					return true;
				}
			}
			return false;
		}
		return false;
	}
	/**
	 * determines the bounds of this terrain object
	 */
	private void determineBounds()
	{
		double lowX = p.getLocation()[0];
		double highX = p.getLocation()[1];
		double lowY = p.getWidth();
		double highY = p.getHeight();
		Iterator<Terrain> i = t.iterator();
		while(i.hasNext())
		{
			Terrain t = i.next();
			Polygon p = t.getPolygon();
			if(p.getLocation()[0] < lowX)
			{
				lowX = p.getLocation()[0];
			}
			if(p.getLocation()[1] < lowY)
			{
				lowY = p.getLocation()[1];
			}
			if(p.getLocation()[0]+p.getWidth() > highX)
			{
				highX = p.getLocation()[0]+p.getWidth();
			}
			if(p.getLocation()[1]+p.getHeight() > highY)
			{
				highY = p.getLocation()[1]+p.getHeight();
			}
		}
		bounds = new RectRegion(lowX, lowY, highX, highY);
	}
	public Polygon getPolygon()
	{
		return p;
	}
	public void drawRegion(GL gl)
	{
		bounds.drawRegion(gl);
	}
	public void fillRegion(GL gl)
	{
		bounds.fillRegion(gl);
	}
	public double getHeight()
	{
		return bounds.getHeight();
	}
	public double[] getLocation()
	{
		return bounds.getLocation();
	}
	public double getWidth()
	{
		return getWidth();
	}
	public boolean intersects(Region r)
	{
		return bounds.intersects(r);
	}
	/**
	 * tests to see if the passed polygon intersects this
	 * terrain feature or its sub feature
	 * @param p
	 * @return returns the given terrain feature that the
	 * passed polygon intersects, null otherwise
	 */
	public Terrain intersects(Polygon p)
	{
		if(bounds.intersects(p))
		{
			if(this.p.intersects(p))
			{
				return this;
			}
			Iterator<Terrain> i = t.iterator();
			while(i.hasNext())
			{
				Terrain t = i.next();
				if(t.getPolygon().intersects(p))
				{
					return t;
				}
			}
			return null;
		}
		return null;
	}
	public void readRegion(DataInputStream dis) throws IOException{}
	public void setLocation(double x, double y)
	{
		bounds.setLocation(x, y);
	}
	public void writeRegion(DataOutputStream dos) throws IOException{}
}
