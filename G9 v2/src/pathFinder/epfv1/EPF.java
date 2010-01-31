package pathFinder.epfv1;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import javax.media.opengl.GL;
import pathFinder.PathFinder;
import com.sun.opengl.util.j2d.TextRenderer;
import utilities.Location;
import utilities.Polygon;

public final class EPF extends PathFinder
{
	private static double minNodeArea = 10; //the smallest a node can be
	
	SNode[] n;
	
	public EPF(int width, int height, Polygon[] p)
	{
		super(width, height, p);
		
		ArrayList<SNode> nodes = new ArrayList<SNode>();
		createNodes(nodes, 0, 0, width, height);
		n = new SNode[nodes.size()];
		Iterator<SNode> i = nodes.iterator();
		int index = 0;
		while(i.hasNext())
		{
			n[index] = i.next();
			index++;
		}
		System.out.println("node count = "+n.length);
		determineAdjacentNodes();
	}
	/**
	 * runs through all the nodes and determines which nodes are "adjacent",
	 * nodes that are adjacent to another node can be reached through that node
	 */
	private void determineAdjacentNodes()
	{
		for(int i = 0; i < n.length; i++)
		{
			ArrayList<SNode> adj = new ArrayList<SNode>();
			for(int a = 0; a < n.length; a++)
			{
				if(a != i)
				{
					if(n[a].isAdjacent(n[i]))
					{
						adj.add(n[a]);
					}
				}
			}
			SNode[] an = new SNode[adj.size()];
			Iterator<SNode> ni = adj.iterator();
			int index = 0;
			while(ni.hasNext())
			{
				an[index] = ni.next();
				index++;
			}
			n[i].setAdjacentNodes(an);
		}
	}
	public Location[] getPath(double sx, double sy, double tx, double ty)
	{
		Location start = new Location(sx, sy);
		Location target = new Location(tx, ty);
		if(start.compareTo(target) == 0)
		{
			Location[] l = {new Location(target.x, target.y)};
			return l;
		}
		else
		{
			//gets the starting and ending nodes
			int sindex = -1; //start index
			int eindex = -1; //end index
			boolean endsFound = false;
			for(int i = 0; i < n.length && !endsFound; i++)
			{
				if(sindex == -1 && n[i].contains(start))
				{
					sindex = i;
				}
				if(eindex == -1 && n[i].contains(target))
				{
					eindex = i;
				}
				if(sindex != -1 && eindex != -1)
				{
					endsFound = true;
				}
			}
			if(endsFound)
			{
				Stack<SNode> path = new Stack<SNode>();
				path.push(n[sindex]);
				HashSet<SNode> fails = new HashSet<SNode>();
				getPath(path, fails, target, n[eindex]);
				
				ArrayList<SNode> newPath = shortenPath(path);
				
				Location[] l = new Location[newPath.size()];
				int index = l.length-1;
				while(index >= 0)
				{
					l[index] = newPath.get(index).getCenter();
					index--;
				}
				return l;
			}
			else
			{
				Location[] l = {start};
				return l;
			}
		}
	}
	/**
	 * shortens the path by removing superfluous sections
	 * @param s
	 */
	private ArrayList<SNode> shortenPath(Stack<SNode> s)
	{
		ArrayList<SNode> path = new ArrayList<SNode>(s);
		HashSet<SNode> pathNodes = new HashSet<SNode>(s);
		HashMap<SNode, Integer> indeces = new HashMap<SNode, Integer>();
		Iterator<SNode> pi = path.iterator();
		int index = 0;
		while(pi.hasNext())
		{
			indeces.put(pi.next(), index);
			index++;
		}
		
		ArrayList<SNode> newPath = new ArrayList<SNode>();
		newPath.add(path.get(0));
		shortenPathHelper(path.get(0), path.get(path.size()-1), path, pathNodes, indeces, newPath);
		return newPath;
	}
	private void shortenPathHelper(SNode start, SNode end, ArrayList<SNode> path, HashSet<SNode> pathNodes, HashMap<SNode, Integer> indeces, ArrayList<SNode> newPath)
	{
		SNode[] n = start.getAdjacentNodes();
		int highestIndex = 0; //where it is in the path
		int moveIndex = 0; //which node the path is going to next of the adjacent ones
		for(int i = 0; i < n.length; i++)
		{
			if(pathNodes.contains(n[i]) && indeces.get(n[i]) > highestIndex)
			{
				highestIndex = indeces.get(n[i]);
				moveIndex = i;
			}
		}
		newPath.add(n[moveIndex]);
		if(n[moveIndex] != end)
		{
			shortenPathHelper(n[moveIndex], end, path, pathNodes, indeces, newPath);
		}
	}
	/**
	 * recursively builds the path
	 * @param path the path
	 * @param checked a set represnting the already tested nodes not to be checked
	 * @param target the target of the path
	 * @param end the end node where the target resides
	 */
	private void getPath(Stack<SNode> path, HashSet<SNode> checked, Location target, SNode end)
	{
		boolean endReached = false;
		while(!endReached)
		{
			SNode[] n = path.peek().getAdjacentNodes();
			int lhindex = -1; //low h index
			double h = -1;
			for(int i = 0; i < n.length && !endReached; i++)
			{
				if(!checked.contains(n[i]))
				{
					if(n[i] == end)
					{
						endReached = true;
					}
					else
					{
						double temp = n[i].getHeuristic(target);
						if(temp < h || h == -1)
						{
							lhindex = i;
							h = temp;
						}
					}
				}
			}
			if(!endReached)
			{
				if(lhindex == -1)
				{
					//all nodes already checked and failed
					checked.add(path.pop());
				}
				else
				{
					path.push(n[lhindex]);
					checked.add(n[lhindex]);
				}
				//getPath(path, checked, target, end);
			}
			else
			{
				path.push(end);
			}
		}
	}
	public void drawPathing(GL gl, boolean drawAdjacencies)
	{
		/*gl.glColor3d(0, 1, 0);
		double depth = 0;
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3d(0, 0, depth);
		gl.glVertex3d(width, 0, depth);
		gl.glVertex3d(width, height, depth);
		gl.glVertex3d(0, height, depth);
		gl.glEnd();*/
		
		gl.glColor3d(1, 0, 0);
		for(int i = 0; i < n.length; i++)
		{
			n[i].drawNode(gl, drawAdjacencies);
		}
		
		Font font = new Font("SansSerif", Font.PLAIN, 12);
	    TextRenderer tr = new TextRenderer(font, true, false);
	    tr.beginRendering(width, height);
	    tr.draw("node count = "+n.length, 10, height-20);
	    tr.endRendering();
	}
	private void createNodes(ArrayList<SNode> n, double x, double y, double width, double height)
	{
		if(width*height > minNodeArea)
		{
			boolean intersects = false;
			
			Location[] vertices = {new Location(0, 0), new Location(width, 0), 
					new Location(width, height), new Location(0, height)};
			Polygon nbounds = new Polygon(new Location(x, y), vertices);
			for(int i = 0; i < p.length && !intersects; i++)
			{
				if(p[i] != null)
				{
					Location c = p[i].getLocation(); //the center of the polygon
					Location[] v = p[i].getVertices();
					for(int a = 0; a < v.length && !intersects; a++)
					{
						if(v[a].x+c.x >= x && v[a].x+c.x <= x+width && v[a].y+c.y >= y && v[a].y+c.y <= y+height)
						{
							intersects = true;
						}
					}
					intersects = nbounds.intersects(p[i]) || intersects;
					//System.out.println(nbounds.intersects(p[i]));
				}
			}
			if(intersects)
			{
				createNodes(n, x, y+height/2, width/2, height/2); //top left
				createNodes(n, x+width/2, y+height/2, width/2, height/2); //top right
				createNodes(n, x+width/2, y, width/2, height/2); //bottom right
				createNodes(n, x, y, width/2, height/2); //bottom left
			}
			else
			{
				n.add(new SNode(x, y, width, height));
			}
		}
	}
}
class SNode
{
	//the x and y coords reprsent the bottom left corner of the node
	private double x;
	private double y;
	private double width;
	private double height;
	
	private SNode[] an; //adjacent nodes
	
	public SNode(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public Location getCenter()
	{
		return new Location(x+width/2, y+height/2);
	}
	public void drawNode(GL gl, boolean drawAdjacencies)
	{
		gl.glColor4d(1, 0, 0, .4);
		/*gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x+width, y);
		gl.glVertex2d(x+width, y+height);
		gl.glVertex2d(x, y+height);
		gl.glEnd();*/
		
		Location[] vertices = {new Location(0, 0), new Location(width, 0), 
				new Location(width, height), new Location(0, height)};
		Polygon nbounds = new Polygon(new Location(x, y), vertices);
		nbounds.drawPolygon(gl, 0);
		
		if(drawAdjacencies)
		{
			gl.glLineWidth(1);
			gl.glColor4d(1, 1, 1, .1);
			Location center = new Location(x+width/2, y+height/2);
			gl.glBegin(GL.GL_LINES);
			for(int i = 0; i < an.length; i++)
			{
				gl.glVertex2d(center.x, center.y);
				gl.glVertex2d(an[i].getCenter().x, an[i].getCenter().y);

				gl.glVertex2d(center.x, center.y+10);
				gl.glVertex2d(an[i].getCenter().x, an[i].getCenter().y);
			}
			gl.glEnd();
		}
	}
	public double getHeuristic(Location target)
	{
		return getCenter().distanceTo(target);
	}
	public boolean contains(Location l)
	{
		if(l.x >= x && l.x <= x+width && l.y >= y && l.y <= y+height)
		{
			return true;
		}
		return false;
	}
	public boolean isAdjacent(SNode n)
	{
		/*if(n.x+n.width == x && n.y+n.height >= y && n.y+n.height <= y+height)
		{
			//left
			return true;
		}*/
		if(n.x+n.width == x && !(n.y+n.height > y+height && n.y > y+height) && !(n.y+n.height < y && n.y < y))
		{
			//left
			return true;
		}
		/*if(n.x == x+width && n.y+n.height >= y && n.y+n.height <= y+height)
		{
			//right
			return true;
		}*/
		if(n.x == x+width && !(n.y+n.height > y+height && n.y > y+height) && !(n.y+n.height < y && n.y < y))
		{
			//left
			return true;
		}
		/*if(n.x+n.width >= x && n.x+n.width <= x+width && n.y == y+height)
		{
			//top
			return true;
		}*/
		if(n.y == y+height && !(n.x+n.width > x+width && n.x > x+width) && !(n.x+n.width < x && n.x < x))
		{
			//top
			return true;
		}
		/*if(n.x+n.width >= x && n.x+n.width <= x+width && n.y+height == y)
		{
			//bottom
			return true;
		}*/
		if(n.y+n.height == y && !(n.x+n.width > x+width && n.x > x+width) && !(n.x+n.width < x && n.x < x))
		{
			//top
			return true;
		}
		return false;
	}
	public void setAdjacentNodes(SNode[] n)
	{
		an = n;
	}
	public SNode[] getAdjacentNodes()
	{
		return an;
	}
	public String toString()
	{
		return "("+x+", "+y+"), w="+width+", h="+height+", center="+getCenter()+", x+w="+(x+width)+", y+h="+(y+height);
		//return "x+w="+(x+width)+", y+h="+(y+height);
	}
}
