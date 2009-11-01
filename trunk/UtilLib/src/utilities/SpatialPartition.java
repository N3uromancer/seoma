package utilities;

import java.awt.Font;
import java.util.HashSet;
import java.util.Iterator;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * creates a spatial partition that auto divides itself into smaller partitions
 * or combines into larger partitions when enough elements are added or removed
 * 
 * if a region's position changes it must be removed then re-added to the partition
 * 
 * change log:
 * -item count dynamically stored
 * 
 * @author Jack
 *
 */
public final class SpatialPartition extends Region
{
	HashSet<Region> r = new HashSet<Region>();
	int minItems;
	int maxItems;
	double minArea;
	int items = 0; //items in the partition
	
	SpatialPartition[] sp; //null when this node is not divided
	
	public static void main(String[] args)
	{
		int regionCount = 1000;
		System.out.println("regions created = "+regionCount);
		
		int width = 2000;
		int height = 2000;
		Region[] regions = new Region[regionCount];
		SpatialPartition sp = new SpatialPartition(0, 0, width, height, 40, 40, 100);
		for(int i = 0; i < regions.length; i++)
		{
			regions[i] = new Region((int)(Math.random()*width), (int)(Math.random()*height), 20, 20);
			sp.addRegion(regions[i]);
		}
		HashSet<Region> rset = sp.getRegions(sp);
		System.out.println("item count = "+sp.size());
		System.out.println("regions actually stored = "+rset.size());
		int missing = 0;
		for(int i = 0; i < regions.length; i++)
		{
			if(!rset.contains(regions[i]))
			{
				System.out.println("missing "+regions[i]);
				missing++;
			}
		}
		System.out.println("total missing regions = "+missing);
		System.out.println("----------------------------");
		
		int regionsToRemove = 100; //the regions to be randomly removed
		System.out.println("random regions to be removed = "+regionsToRemove);
		int[] toRemove = new int[regionsToRemove];
		HashSet<Integer> indeces = new HashSet<Integer>();
		for(int i = 0; i < regionsToRemove; i++)
		{
			int index = -1;
			while(index == -1 || !indeces.contains(index))
			{
				index = (int)(Math.random()*regionCount);
				if(!indeces.contains(index))
				{
					indeces.add(index);
				}
			}
			toRemove[i] = index;
		}
		for(int i = 0; i < toRemove.length; i++)
		{
			boolean removed = sp.removeRegion(regions[toRemove[i]]);
			if(!removed)
			{
				System.out.println(removed);
			}
		}
		System.out.println("item count = "+sp.size());
		rset = sp.getRegions(sp);
		System.out.println("regions actually stored = "+rset.size());
	}
	/**
	 * creates a new spatial partition
	 * @param x x position of the lower left corner of the region
	 * @param y
	 * @param width
	 * @param height
	 * @param minItems min items in region before removing one combines the partition
	 * @param maxItems max items in region before division into 4 sub divisions
	 * @param minArea min area of the spatial partition before it stops dividing itself
	 */
	public SpatialPartition(int x, int y, int width, int height, int minItems, int maxItems, double minArea)
	{
		super(x, y, width, height);
		this.minItems = minItems;
		this.maxItems = maxItems;
		this.minArea = minArea;
	}
	/**
	 * checks to see if this partition is divided
	 * @return returns true if this partition is subdivided into 4 other partitions,
	 * false otherwise
	 */
	public boolean isDivided()
	{
		return r==null;
	}
	public void addRegion(Region region)
	{
		if(intersects(region))
		{
			items++;
			//System.out.println("INTERSECTS "+toString());
			if(r != null)
			{
				if(!r.contains(region))
				{
					//System.out.println("ADDED to "+toString());
					r.add(region);
					//System.out.println("items = "+items);
					if(r.size() > maxItems)
					{
						//System.out.println("max exceeded ("+items+" ("+r.size()+")/ "+maxItems+"), dividing partition");
						dividePartition();
					}
				}
			}
			else
			{
				//System.out.println("adding to SUBDIVISION...");
				for(int i = sp.length-1; i >= 0; i--)
				{
					sp[i].addRegion(region);
				}
			}
		}
	}
	public boolean removeRegion(Region region)
	{
		boolean removed = false; //true if it removed the region
		
		if(intersects(region))
		{
			items--;
			if(r != null)
			{
				removed = r.remove(region);
			}
			else
			{
				int totalItems = 0;
				for(int i = sp.length-1; i >= 0; i--)
				{
					totalItems+=sp[i].size();
				}
				//System.out.println("total items = "+totalItems);
				if(totalItems < minItems)
				//if(items < minItems)
				{
					//System.out.println("below min level");
					//combines the partition
					boolean sameLevel = true;
					for(int i = sp.length-1; i >= 0 && sameLevel; i--)
					{
						if(sp[i].isDivided())
						{
							//System.out.println(sp[i]+" is divided");
							sameLevel = false;
						}
					}
					if(sameLevel)
					{
						//System.out.println("all on same level, combining...");
						r = getRegions(this);
						sp = null;
						//items = r.size();
						//System.out.println("r.size() = "+r.size());
					}
				}
				if(sp != null)
				{
					for(int i = sp.length-1; i >= 0; i--)
					{
						removed = sp[i].removeRegion(region) || removed;
					}
				}
				else
				{
					removed = r.remove(region) || removed;
				}
			}
		}
		return removed;
	}
	/**
	 * gets all the regions in this partition
	 * @return returns all the regions in this partition
	 */
	private HashSet<Region> getRegions(SpatialPartition sp)
	{
		//System.out.println(this);
		if(sp.r != null)
		{
			return sp.r;
		}
		else
		{
			HashSet<Region> r = new HashSet<Region>();
			for(int i = sp.sp.length-1; i >= 0; i--)
			{
				r.addAll(getRegions(sp.sp[i]));
			}
			return r;
		}
	}
	/**
	 * gets the number of items that reside in this node or the quad
	 * tree it represents, items can be counted twice because they
	 * may reside in more than one node
	 * @return returns the number of items in the partition
	 */
	public int size()
	{
		return items;
	}
	/**
	 * gets the total number of items, items are not counted twice
	 * @return returns the nunmber of items in the partition
	 */
	public int trueSize()
	{
		return getRegions(this).size();
	}
	/**
	 * divides the partition into a smaller partition
	 */
	private void dividePartition()
	{
		//items = 0;
		if(width/2*height/2 >= minArea)
		{
			sp = new SpatialPartition[4];
			sp[0] = new SpatialPartition((int)x, (int)y, (int)(width/2), (int)(height/2), minItems, maxItems, minArea);
			sp[1] = new SpatialPartition((int)(x+width/2), (int)y, (int)(width/2), (int)(height/2), minItems, maxItems, minArea);
			sp[2] = new SpatialPartition((int)(x+width/2), (int)(y+height/2), (int)(width/2), (int)(height/2), minItems, maxItems, minArea);
			sp[3] = new SpatialPartition((int)x, (int)(int)(y+height/2), (int)(width/2), (int)(height/2), minItems, maxItems, minArea);
			
			/*System.out.println("=========== new partitions ===========");
			for(int i = sp.length-1; i >= 0; i--)
			{
				System.out.println(sp[i]);
			}
			System.out.println("======================================");*/
			
			Iterator<Region> ri = r.iterator();
			while(ri.hasNext())
			{
				Region region = ri.next();
				for(int i = sp.length-1; i >= 0; i--)
				{
					sp[i].addRegion(region);
				}
			}
			r = null;
		}
	}
	/**
	 * draws the partition for debug purposes
	 * @param gl
	 * @param swidth the width of the screen, used for the purpose of
	 * drawing text
	 * @param sheight the height of the screen, used for the purpose of
	 * drawing text
	 */
	public void drawPartition(GL gl, int swidth, int sheight)
	{
	    if(r != null)
	    {
			Font font = new Font("SansSerif", Font.PLAIN, 12);
	    	TextRenderer tr = new TextRenderer(font, true, false);
			drawRegion(gl);
			tr.beginRendering(swidth, sheight);
			tr.draw(""+r.size(), (int)x, (int)y+10);
			tr.endRendering();
	    }
		if(sp != null)
		{
			for(int i = sp.length-1; i >= 0; i--)
			{
				sp[i].drawPartition(gl, swidth, sheight);
			}
		}
	}
	/**
	 * returns all regions that intersect with the passed region
	 * @param x the bottom left x coord of the region
	 * @param y the bottom left y coord of the region
	 * @param width the width of the region
	 * @param height the height of the region
	 * @return returns all regions that intersect with the passed region
	 */
	public HashSet<Region> getIntersections(double x, double y, double width, double height)
	{
		return getIntersections(new Region(x, y, width, height));
	}
	/**
	 * returns all regions that intersect with the passed region
	 * @param region
	 * @return returns all regions that intersect with the passed region
	 */
	public HashSet<Region> getIntersections(Region region)
	{
		HashSet<Region> intersections = new HashSet<Region>();
		getItersectionsHelper(intersections, region, this);
		return intersections;
	}
	/**
	 * recursively evaluates the quad tree searching for intersections
	 * @param intersections
	 * @param region
	 */
	private void getItersectionsHelper(HashSet<Region> intersections, Region region, SpatialPartition sp)
	{
		if(intersects(region))
		{
			if(sp.r != null)
			{
				Iterator<Region> i = sp.r.iterator();
				while(i.hasNext())
				{
					Region rtemp = i.next();
					if(!intersections.contains(rtemp) && rtemp.intersects(region))
					{
						intersections.add(rtemp);
					}
				}
			}
			else
			{
				for(int i = sp.sp.length-1; i >= 0; i--)
				{
					getItersectionsHelper(intersections, region, sp.sp[i]);
				}
			}
		}
	}
	/**
	 * gets the regions that are in a redius around the passed location, regions are found by
	 * first found by calling the getItersections method using a region whose width = radius*2,
	 * next an iterator goes through finding only those that are actually in range
	 * @param x the center of the circle
	 * @param y
	 * @param radius
	 * @return returns a hashset of regions whose center is within the passed radius of the passed
	 * position
	 */
	public HashSet<Region> getRegions(double x, double y, double radius)
	{
		Region bounds = new Region(x-radius, y-radius, radius*2, radius*2);
		HashSet<Region> r = getIntersections(bounds);
		
		HashSet<Region> regions = new HashSet<Region>();
		Iterator<Region> i = r.iterator();
		while(i.hasNext())
		{
			Region rtemp = i.next();
			if(!regions.contains(rtemp) && MathUtil.distance(x, y, rtemp.x+rtemp.width/2, rtemp.y+rtemp.height/2) <= radius)
			{
				regions.add(rtemp);
			}
		}
		
		return regions;
	}
}
