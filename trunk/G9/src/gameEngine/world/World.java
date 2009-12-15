package gameEngine.world;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import gameEngine.StartSettings;
import gameEngine.world.owner.Owner;
import gameEngine.world.resource.ResourceEngine;
import gameEngine.world.shot.Shot;
import gameEngine.world.shot.ShotEngine;
import gameEngine.world.unit.Unit;
import gameEngine.world.unit.UnitEngine;
import javax.media.opengl.GL;

import mapEditor.Map;

import ai.AI;
import pathFinder.*;
import pathFinder.epfv2.EPFV2;
import ui.userIO.userInput.UserInput;
import utilities.Polygon;
import utilities.region.RectRegion;
import utilities.region.Region;

/**
 * holds information about the game world, presents a front that routes game information
 * to wherever it needs to be via the register method (shots automatically sent to shot engine,
 * units automatically sent to unit engine)
 * @author Jack
 *
 */
public class World
{
	String name;
	String description;
	
	ShotEngine se;
	UnitEngine ue;
	ResourceEngine re;
	
	Owner[] o;
	ArrayList<Polygon> p = new ArrayList<Polygon>();
	ArrayList<Region> startLocations = new ArrayList<Region>();
	//Polygon[] p = new Polygon[10];
	PathFinder pf;
	HashMap<Owner, AI> ais;
	
	int width;
	int height;
	
	public ShotEngine getShotEngine()
	{
		return se;
	}
	public Owner[] getOwners()
	{
		return o;
	}
	public ResourceEngine getResourceEngine()
	{
		return re;
	}
	public HashMap<Owner, AI> getAIs()
	{
		return ais;
	}
	public World(StartSettings ss, HashMap<Owner, AI> ais)
	{
		this.ais = ais;
		width = ss.getMapWidth();
		height = ss.getMapHeight();
		
		/*for(int i = 0; i < p.length; i++)
		{
			double x = Math.random()*width;
			double y = Math.random()*height;
			//Location[] vertices = {new Location(x, y), new Location(x+Math.random()*70, y), new Location(x, y+Math.random()*80)};
			Location[] vertices = {new Location(x, y), new Location(x, y+Math.random()*80), 
					new Location(x+Math.random()*90, y+Math.random()*120), new Location(x+Math.random()*70, y)};
			//p[i] = new Polygon(new Location(x, y), vertices);
			p[i] = new Polygon(Polygon.determineBoundingRegion(vertices), vertices);
		}*/
		Map m = new Map();
		try
		{
			File f = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+
					System.getProperty("file.separator")+"maps"+System.getProperty("file.separator")+
					"a test map with resources.map");
			FileInputStream fis = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(fis);
			m.readMap(dis);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		p = m.getPolygons();
		startLocations = m.getStartLocations();
		
		se = new ShotEngine(ss, p.toArray(new Polygon[p.size()]));
		ue = new UnitEngine(ss, this);
		re = new ResourceEngine(m.getResourceDeposits());
		
		
		this.o = ss.getOwners();
		
		pf = new EPFV2(width, height, p.toArray(new Polygon[p.size()]));
	}
	public ArrayList<Region> getStartLocations()
	{
		return startLocations;
	}
	public PathFinder getPathFinder()
	{
		return pf;
	}
	public UnitEngine getUnitEngine()
	{
		return ue;
	}
	public int getMapWidth()
	{
		return width;
	}
	public int getMapHeight()
	{
		return height;
	}
	/**
	 * tests to see if the passed location is inside the game world
	 * @param x
	 * @param y
	 * @return returns true if the passed location is in the game world,
	 * false otherwise
	 */
	public boolean inWorld(double x, double y)
	{
		return new RectRegion(0, 0, width, height).contains(x, y);
	}
	public void updateWorld(double tdiff, HashMap<Byte, ArrayList<UserInput>> ui)
	{
		for(int i = o.length-1; i >= 0; i--)
		{
			ais.get(o[i]).performAIFunctions(this, ui.get(o[i].getID()));
		}
		ue.updateUnitEngine(tdiff, this);
		se.updateShotEngine(tdiff, ue);
		re.updateResourceEngine(tdiff, this);
	}
	/**
	 * draws the world
	 * @param owner the owner of the game engine that is drawing the world,
	 * the ui of the ai belonging to this passed owner is drawn
	 * @param dwidth width of the displayed region of the screen
	 * @param dheight height of the displayed region of the screen
	 * @param gl
	 */
	public void drawWorld(Owner owner, int dwidth, int dheight, GL gl)
	{
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, dwidth, 0, dheight, -1, 1);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		int x = 0;
		int y = 0;
		
		HashSet<Region> r = ue.getAllUnits().getIntersections(x, y, dwidth, dheight);
		Iterator<Region> i = r.iterator();
		while(i.hasNext())
		{
			Unit u = (Unit)i.next();
			u.draw(gl);
		}
		r = se.getShots().getIntersections(x, y, dwidth, dheight);
		i = r.iterator();
		while(i.hasNext())
		{
			Shot s = (Shot)i.next();
			s.drawShot(gl);
			if(s.isDead())
			{
				//System.out.println("dead and should not be drawn...");
			}
		}
		//se.getShotPartition().drawPartition(gl, width, height);
		
		re.drawResourceDeposits(gl);
	
		/*gl.glLineWidth(1);
		for(int a = 0; a < p.length; a++)
		{
			gl.glColor4d(0, 1, 0, .6);
			p[a].drawPolygon(gl, .1);
		}*/
		gl.glLineWidth(1);
		gl.glColor4d(0, 1, 0, .6);
		Iterator<Polygon> pi = p.iterator();
		while(pi.hasNext())
		{
			pi.next().drawPolygon(gl, .1);
		}
		
		//gl.glLineWidth(1);
		gl.glColor4d(1, 1, 1, .3);
		pf.drawPathing(gl, false);
		
		if(ais.get(owner) != null)
		{
			ais.get(owner).drawUI(gl);
		}
		
		gl.glClearColor(0, 0, 0, 1);
	}
}
