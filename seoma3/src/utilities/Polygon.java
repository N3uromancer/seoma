package utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.media.opengl.GL;

import utilities.region.RectRegion;

/**
 * defines a convex polygon
 * 
 * change log:
 * version 2:
 * -polygon now stored as a region
 * -regional intersection is checked first now before polygnal intersection
 * when determining if a polygon is contained inside another polygon
 * -rotation feature removed because it was buggy and unnecesary for now
 * -added a method to draw normal vectors
 * -added read and write methods for saving and loading the state of a polygon
 * 
 * @author Jack
 *
 */
public class Polygon extends RectRegion
{
	private Location[] vertices;
	/**
	 * stores the normals for this polygon, all normals are directed
	 * away from the center
	 */
	private Location[] normal;
	
	public static void main(String[] args)
	{
		double x = 300;
		double y = 300;
		final Location[] v = {new Location(x+40, y+40), new Location(x+70, y+170), new Location(x+140, y+40)};
		//final Polygon p = new Polygon(Polygon.determineBoundingRegion(v), v);
		final Polygon p = new Polygon(v);
		System.out.println("polygon bounds = "+p);
		
		x+=40;
		y+=40;
		final Location[] v2 = {new Location(x+40, y+40), new Location(x+70, y+170), new Location(x+140, y+40)};
		//final Polygon p2 = new Polygon(Polygon.determineBoundingRegion(v2), v2);
		final Polygon p2 = new Polygon(v2);
		
		double width = 40;
		double height = 70;
		Location[] vertices = {new Location(x, y), new Location(x, y+height), new Location(x+width, y+height), new Location(x+width, y)};
		//final Polygon p3 = new Polygon(Polygon.determineBoundingRegion(vertices), vertices);
		final Polygon p3 = new Polygon(vertices);
		
		new Sandbox(){
			public void draw(GL gl, int width, int height)
			{
				gl.glMatrixMode(GL.GL_PROJECTION);
				gl.glLoadIdentity();
				gl.glOrtho(0, width, 0, height, -1, 1);
				
				gl.glColor3d(1, 1, 1);
				//Location[] v = {new Location(40, 40), new Location(140, 40), new Location(70, 170)};
				//Polygon p = new Polygon(Polygon.determineBoundingRegion(v), v);
				p.drawPolygon(gl, 1);
				gl.glColor3d(1, 0, 0);
				//p.drawRegion(gl);
				p.drawNormalVectors(gl, 20, 0);
				
				gl.glColor3d(0, 1, 0);
				if(p3.intersects(p))
				{
					gl.glColor3d(0, 0, 1);
				}
				p3.drawPolygon(gl, 1);
				p3.drawNormalVectors(gl, 20, 1);

				gl.glColor3d(1, 1, 1);
				p2.drawPolygon(gl, 0);
			}
		};
	}
	/**
	 * returns the center of the polygon by averaging all the vertex offsets
	 * then adding the translational amount of the polygon
	 * @return returns the center of the polygon
	 */
	public double[] getCenter()
	{
		double x = 0;
		double y = 0;
		for(int i = 0; i < vertices.length; i++)
		{
			x+=vertices[i].x;
			y+=vertices[i].y;
		}
		return new double[]{getLocation()[0]+x/vertices.length, getLocation()[1]+y/vertices.length};
	}
	/**
	 * creates a new polygon, the locations of the vertices must be given
	 * relative to the center
	 * @param bounds can be computed using that polygon static method determineBoundingRegion
	 * @param vertices vertex locations relative to the bounds, vertices must be defined in
	 * counter clockwise order or the normals will be incorrectly calculated
	 */
	public Polygon(Location[] vertices)
	{
		//super(bounds.getLocation()[0], bounds.getLocation()[1], bounds.getWidth(), bounds.getHeight());
		super(determineBoundingRegion(vertices));
		this.vertices = vertices;
		offSetVertices();
		
		normal = new Location[vertices.length];
		for(int i = 0; i < vertices.length; i++)
		{
			int i2 = i+1;
			if(i+1 == vertices.length)
			{
				i2 = 0;
			}
			//Location l3 = new Location(vertices[i].x, vertices[i].y, vertices[i].z-1);
			//normal[i] = determineNormal(vertices[i], vertices[i2], l3);
			
			double[] n = MathUtil.normal(vertices[i].x, vertices[i].y, vertices[i2].x, vertices[i2].y);
			normal[i] = new Location(n[0], n[1]);
		}
	}
	/**
	 * offsets the vertices so that they store only the difference in
	 * their position from the center (the bottom left corner of the bounds)
	 */
	public void offSetVertices()
	{
		for(int i = 0; i < vertices.length; i++)
		{
			vertices[i] = new Location(vertices[i].x-x, vertices[i].y-y);
		}
	}
	public static RectRegion determineBoundingRegion(Location[] vertices)
	{
		//for determining the bounding rectanle
		double lowx = 0;
		double lowy = 0;
		double highx = 0;
		double highy = 0;
		
		for(int i = 0; i < vertices.length; i++)
		{
			if(vertices[i].x < lowx || i == 0)
			{
				lowx = vertices[i].x;
			}
			if(vertices[i].x > highx || i == 0)
			{
				highx = vertices[i].x;
			}
			if(vertices[i].y < lowy || i == 0)
			{
				lowy = vertices[i].y;
			}
			if(vertices[i].y > highy || i == 0)
			{
				//System.out.println(vertices[i].y+" > "+highy+" || i ("+i+") == 0");
				highy = vertices[i].y;
			}
		}
		
		//System.out.println("lowx = "+lowx+", lowy = "+lowy+", highx = "+highx+", highy = "+highy);
		
		double width = highx - lowx;
		double height = highy - lowy;
		
		return new RectRegion(lowx, lowy, width, height);
	}
	/**
	 * returns the vertices of this polygon
	 * @return
	 */
	public Location[] getVertices()
	{
		return vertices;
	}
	/**
	 * tests to see if the polygon cantains the passed point
	 * @param x
	 * @param y
	 * @return returns true if the polygon contains the point, false otherwise
	 */
	public boolean contains(double x, double y)
	{
		for(int i = 0; i < vertices.length; i++)
		{
			int i2 = i+1;
			if(i+1 == vertices.length)
			{
				i2 = 0;
			}
			
			double[] v1 = {vertices[i].x, vertices[i].y}; //the start pos of vector 1
			v1[0]+=this.x;
			v1[1]+=this.y;
			double[] v1t = {vertices[i2].x-vertices[i].x, vertices[i2].y-vertices[i].y}; //trans amount for vector 1
			
			double[] v2t = {normal[i].x, normal[i].y}; //translational amount for vector 2
			
			boolean failed = false;
			double[] v2 = {x, y}; //vector 2 start pos
			
			double alpha = calculateAlpha(v1, v1t, v2, v2t);
			if(alpha > 0)
			{
				failed = true;
			}
			if(!failed)
			{
				return false;
			}
		}
		return true;
	}
	/**
	 * checks to see if this polygon contains the passed one, for a polygon
	 * to contain another all of the others vertices must be inside the polygon
	 * doing the containing
	 * @param p
	 * @return
	 */
	public boolean contains(Polygon p)
	{
		if(intersects(p))
		{
			Location c = new Location(p.x, p.y); //the center of the other polygon
			Location[] v = p.getVertices();
			for(int i = 0; i < v.length; i++)
			{
				if(!contains(c.x+v[i].x, c.y+v[i].y))
				{
					//System.out.println(c+" + "+v[i]+" is not contained");
					return false;
				}
			}
			return true;
		}
		return false;
	}
	/**
	 * determines if a given polygon intersects this one, a polygon does 
	 * not intersect another polygon if a separating plane between the two
	 * polygons can be found
	 * @param p the other polygon being tested for possible intersection
	 * with this polygon
	 * @return returns true if the passed polygon intersects this polygon,
	 * false otherwise
	 */
	public boolean intersects(Polygon p)
	{
		/*
		 * both polygons must be tested if the first test indicates that the
		 * polygons intersect because a separating plane may exist on the
		 * other polygon
		 */
		boolean intersects = intersectsHelper(p);
		if(!intersects)
		{
			return false;
		}
		else
		{
			boolean otherIntersects = p.intersectsHelper(this);
			if(!otherIntersects)
			{
				return false;
			}
		}
		return true;
	}
	/**
	 * the helper method where the actual calculations are done
	 * @param p
	 * @return returns true if the passed polygon intersects this one
	 */
	private boolean intersectsHelper(Polygon p)
	{
		for(int i = 0; i < vertices.length; i++)
		{
			int i2 = i+1;
			if(i+1 == vertices.length)
			{
				i2 = 0;
			}
			
			/*
			 * the vector representing the side of this polygon currently being tried
			 * as a possible separating plane, [x,y]=v1+L*v1t
			 */
			double[] v1 = {vertices[i].x, vertices[i].y}; //the start pos of vector 1
			v1[0]+=this.x;
			v1[1]+=this.y;
			double[] v1t = {vertices[i2].x-vertices[i].x, vertices[i2].y-vertices[i].y}; //trans amount for vector 1
			
			//System.out.println("plane "+(i+1)+":");
			//System.out.println("[x,y] = ["+v1[0]+", "+v1[1]+"] + L["+v1t[0]+", "+v1t[1]+"]");
			//System.out.println("beginning eval...\n");
			
			double[] v2t = {normal[i].x, normal[i].y}; //translational amount for vector 2
			
			Location[] vertices2 = p.getVertices();
			Location center2 = new Location(p.x, p.y);
			boolean failed = false;
			for(int q = 0; q < vertices2.length && !failed; q++)
			{
				double[] v2 = {vertices2[q].x, vertices2[q].y}; //vector 2 start pos
				v2[0]+=center2.x;
				v2[1]+=center2.y;
				
				//System.out.println("intersecting vector:");
				//System.out.println("[x,y] = ["+v2[0]+", "+v2[1]+"] + A["+v2t[0]+", "+v2t[1]+"]");
				
				double alpha = calculateAlpha(v1, v1t, v2, v2t);
				//System.out.println("alpha = "+alpha);
				if(alpha > 0)
				{
					/*
					 * must be negative because the intersecting vector (from a vertex
					 * on the other polygon) must move in the opposite direction from
					 * the normal of the surface currently being evaluted of this polygon
					 * 
					 * all the normals point away from the center of the polygon
					 */
					failed = true;
					//System.out.println("failed\n");
				}
			}
			if(!failed)
			{
				/*
				 * all vertices of other polygon lie on the other side of an
				 * arbitrary bounding plane, not intersecting
				 */
				//System.out.println("NOT intersecting");
				return false;
			}
		}
		/*
		 * checked all possible planes (based off the sides of this polygon)
		 * and determined that no bounding plane exists
		 */
		//System.out.println("INTERSECTING!");
		return true;
	}
	/**
	 * calculates the amount and direction traveled along the vector v2 until
	 * it intersects with vector v1, used to determine which side of the plane
	 * represented by v1 and v1t a point (v2) lies on, solves for the tranlational
	 * amount (alpha, A) in the following system: [x,y]=v1+L*v1t [x,y]=v2+A*v2t
	 * @param v1 defines the starting point for the vector reprsenting the side
	 * of this polygon that is currently being evaulated
	 * @param v1t the translational vector representing the side of this polygon
	 * currently being evaluated
	 * @param v2 the starting location of the second vector, perpendicular to
	 * vector v1, when evaulating a point to see which side of the surface of the
	 * polygon currently being evaulated
	 * @param v2t the normal vector for the plane being evaluated ( [x,y]=v1+L*v1t )
	 * @return calculates the direction and amount traveled by a point at location
	 * v2 that moves in the most direct way towards the plane [x,y]=v1+L*v1t,
	 * solves for the tranlational amount (alpha, A) in the following system:
	 * [x,y]=v1+L*v1t [x,y]=v2+A*v2t
	 */
	private double calculateAlpha(double[] v1, double[] v1t, double[] v2, double[] v2t)
	{
		//solves [x,y]=v1+L*v1t [x,y]=v2+A*v2t for A
		double alpha = 0;
		if(v1t[0] == 0)
		{
			/*
			 * x part of the tranlsational vector v1t in [x,y]=v1+L*v1t is zero
			 * thus simplfications can be made
			 */
			alpha = (v1[0]-v2[0])/v2t[0];
		}
		else if(v1t[1] == 0)
		{
			//same as above but for y part
			alpha = (v1[1]-v2[1])/v2t[1];
		}
		else
		{
			double lambda = (v2[0]-v1[0])/v1t[0];
			double alphaCoef = v2t[0]/v1t[0];
			alpha = v1[1]+v1t[1]*lambda-v2[1];
			if(alpha != 0)
			{
				alphaCoef*=v1t[1];
				alphaCoef = v2t[1]-alphaCoef;
				alpha/=alphaCoef;
			}
		}
		return alpha;
	}
	/*/**
	 * determines the normal of the surface defined by the three points
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return returns a normal vector
	 */
	/*private Location determineNormal(Location p1, Location p2, Location p3)
	{
		Location v1 = new Location(p2.x-p1.x, p2.y-p1.y, p2.z-p1.z); //vector 1
		Location v2 = new Location(p3.x-p1.x, p3.y-p1.y, p3.z-p1.z);
		Location normal = new Location(v1.y*v2.z-v2.y*v1.z, v2.x*v1.z-v1.x*v2.z, v1.x*v2.y-v2.x*v1.y);
		double length = Math.sqrt(Math.pow(normal.x, 2)+Math.pow(normal.y, 2)+Math.pow(normal.z, 2));
		normal = new Location(normal.x/length, normal.y/length, normal.z/length);
		return normal;
	}*/
	public void fillPolygon(GL gl, double depth)
	{
		gl.glPushMatrix();
		gl.glTranslated(x, y, 0);
		gl.glBegin(GL.GL_POLYGON);
		drawHelper(gl, depth);
		gl.glEnd();
		gl.glPopMatrix();
	}
	public void drawPolygon(GL gl, double depth)
	{
		gl.glPushMatrix();
		gl.glTranslated(x, y, 0);
		gl.glBegin(GL.GL_LINE_LOOP);
		drawHelper(gl, depth);
		gl.glEnd();
		
		gl.glPopMatrix();
	}
	/**
	 * draws the normal vectors for the sides of the polygon
	 * @param gl
	 * @param vlength the length of the normal vectors
	 * @param depth
	 */
	public void drawNormalVectors(GL gl, double vlength, double depth)
	{
		gl.glBegin(GL.GL_LINES);
		for(int i = 0; i < vertices.length; i++)
		{
			int index2 = i+1;
			if(index2 == vertices.length)
			{
				index2 = 0;
			}
			
			double[] s = MathUtil.midpoint(vertices[i].x, vertices[i].y, vertices[index2].x, vertices[index2].y);
			s[0]+=x;
			s[1]+=y;

			/*double[] s2 = MathUtil.midpoint(vertices[i].x, vertices[i].y, vertices[index2].x, vertices[index2].y);
			//System.arraycopy(s, 0, s2, 0, 2);
			s2[0]+=normal[i].x*10+x;
			s2[1]+=normal[i].y*10+y;*/
			
			/*double[] s = {vertices[i].x, vertices[i].y};
			s[0]+=x;
			s[1]+=y;*/
			
			double[] s2 = {s[0]+normal[i].x*vlength, s[1]+normal[i].y*vlength};
			
			gl.glVertex3d(s[0], s[1], depth);
			gl.glVertex3d(s2[0], s2[1], depth);
		}
		gl.glEnd();
	}
	private void drawHelper(GL gl, double depth)
	{
		/*for(int i = vertices.length-1; i >= 0; i--)
		{
			gl.glVertex3d(vertices[i].x+center.x, vertices[i].y+center.y, depth);
		}*/
		for(int i = vertices.length-1; i >= 0; i--)
		{
			gl.glVertex3d(vertices[i].x, vertices[i].y, depth);
		}
	}
	/**
	 * writes the passed polygon's state to the passed stream
	 * @param p
	 * @param dos
	 * @throws IOException
	 */
	public static void writePolygon(Polygon p, DataOutputStream dos) throws IOException
	{
		dos.writeInt(p.vertices.length);
		for(int i = 0; i < p.vertices.length; i++)
		{
			dos.writeDouble(p.vertices[i].x+p.x);
			dos.writeDouble(p.vertices[i].y+p.y);
		}
	}
	/**
	 * loads a polygon from the passed stream
	 * @param dis
	 * @return returns a poolygon whose state was read from the
	 * passed input stream
	 * @throws IOException
	 */
	public static Polygon readPolygon(DataInputStream dis) throws IOException
	{
		int length = dis.readInt();
		//System.out.println("length = "+length);
		Location[] vertices = new Location[length];
		for(int i = 0; i < vertices.length; i++)
		{
			double x = dis.readDouble();
			double y = dis.readDouble();
			vertices[i] = new Location(x, y);
		}
		//return new Polygon(determineBoundingRegion(vertices), vertices);
		return new Polygon(vertices);
	}
}
