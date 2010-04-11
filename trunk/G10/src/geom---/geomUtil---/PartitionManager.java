package geom.geomUtil;

import geom.Boundable;
import geom.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class PartitionManager
{
	/**
	 * the base partition
	 */
	private SpatialPartition base;
	private int maxElements;
	private int minElements;
	private int minArea;
	
	/**
	 * stores the partitions of each element
	 */
	private HashMap<Boundable, HashSet<SpatialPartition>> m = new HashMap<Boundable, HashSet<SpatialPartition>>();
	/**
	 * stores the elements stored of each spatial partition
	 */
	private HashMap<SpatialPartition, HashSet<Boundable>> elements = new HashMap<SpatialPartition, HashSet<Boundable>>();
	
	/**
	 * creates a new partition manager
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param minElements
	 * @param maxElements
	 * @param minArea the minimum area a partition must have in order to divide itself
	 */
	public PartitionManager(double x, double y, double width, double height, int minElements, int maxElements, int minArea)
	{
		base = new SpatialPartition(null, x, y, width, height);
		elements.put(base, new HashSet<Boundable>());
		this.minElements = minElements;
		this.maxElements = maxElements;
		this.minArea = minArea;
	}
	/**
	 * checks to see if the passed boundable object is contained within the partition, O(1)
	 * @param b
	 * @return returns true if the object is contained within the partition, false otherwise
	 */
	public boolean contains(Boundable b)
	{
		return m.containsKey(b);
	}
	/**
	 * gets a set containing all the elements sorted by this partition manager
	 * @return returns a set that contains all elements contained in this partition manager
	 */
	public Set<Boundable> getElements()
	{
		return m.keySet();
	}
	public void add(Boundable b)
	{
		ArrayList<SpatialPartition> toSplit = new ArrayList<SpatialPartition>(); //partitions to split
		HashSet<SpatialPartition> sp = base.add(b.getBounds());
		if(sp.size() > 0)
		{
			m.put(b, sp);
			for(SpatialPartition temp: sp)
			{
				elements.get(temp).add(b);
				if(elements.get(temp).size() > maxElements && temp.getWidth()*temp.getHeight() >= minArea)
				{
					toSplit.add(temp);
				}
			}
		}
		//collapses the partitions marked for split, done separately to avoid concurrent modifcation exceptions
		if(toSplit.size() > 0)
		{
			for(SpatialPartition temp: toSplit)
			{
				split(temp);
			}
		}
	}
	/**
	 * gets the number of elements the partition manager currently holds
	 * @return returns the size
	 */
	public int size()
	{
		return m.size();
	}
	/**
	 * splits the passed partition and replaces its elements into the corrent partitions,
	 * removes the split partition from both maps
	 * @param sp
	 */
	private void split(SpatialPartition sp)
	{
		SpatialPartition[] sub = sp.split();
		for(int i = 0; i < sub.length; i++)
		{
			elements.put(sub[i], new HashSet<Boundable>());
		}
		for(Boundable b: elements.get(sp))
		{
			m.get(b).remove(sp);
			//adds boundable objects to the newly created partitions
			for(int i = 0; i < sub.length; i++)
			{
				if(sub[i].intersects(b.getBounds()))
				{
					elements.get(sub[i]).add(b);
					m.get(b).add(sub[i]);
				}
			}
		}
		elements.remove(sp);
		/*if(sp == base)
		{
			System.out.println("BASE REMOVED, "+sp);
		}*/
	}
	/**
	 * removes the passed boundable object from all partitions very quickly
	 * @param b
	 */
	public void remove(Boundable b)
	{
		if(m.containsKey(b))
		{
			ArrayList<SpatialPartition> toCollapse = null; //partitions to collapse
			Iterator<SpatialPartition> i = m.get(b).iterator();
			while(i.hasNext())
			{
				SpatialPartition temp = i.next();
				elements.get(temp).remove(b);
				if(temp.getParent() != null && elements.get(temp).size() < minElements && shouldCollapse(temp))
				{
					if(toCollapse == null)
					{
						toCollapse = new ArrayList<SpatialPartition>();
					}
					toCollapse.add(temp);
				}
			}
			m.remove(b);
			//collapses the partitions marked for collapse, done separately to avoid concurrent modifcation exceptions
			if(toCollapse != null && toCollapse.size() > 0)
			{
				for(SpatialPartition temp: toCollapse)
				{
					if(elements.containsKey(temp))
					{
						collapse(temp);
					}
				}
			}
		}
	}
	/**
	 * checks to see if the passed partition should be collapsed, partitions are only
	 * collapsed if the children of their parent node all contain less than the minimum
	 * number of elements
	 * @param sp
	 * @return returns true if the partition should be collapsed, false otherwise
	 */
	private boolean shouldCollapse(SpatialPartition sp)
	{
		SpatialPartition parent = sp.getParent();
		elements.put(parent, new HashSet<Boundable>());
		SpatialPartition[] children = parent.getChildren();
		
		try
		{
			for(int i = 0; i < children.length; i++)
			{
				//"children[i].getChildren() != null" garentees other nodes are on the same level
				if(children[i].getChildren() != null || elements.get(children[i]).size() > minElements)
				{
					return false;
				}
			}
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
			System.out.println("sp = "+sp);
			System.out.println("parent = "+parent);
			for(int i = 0; i < children.length; i++)
			{
				if(elements.get(children[i]) == null)
				{
					System.out.println("null, "+children[i]);
				}
			}
			System.out.println("----------------");
			for(SpatialPartition temp: elements.keySet())
			{
				System.out.println(temp+", contains "+elements.get(temp).size()+" items");
			}
		}
		return true;
	}
	/**
	 * collapses the passed node and moves its elements to the parent node,
	 * collapsing is expensive
	 * @param sp
	 */
	private void collapse(SpatialPartition sp)
	{
		SpatialPartition parent = sp.getParent();
		elements.put(parent, new HashSet<Boundable>());
		/*if(parent == base)
		{
			System.out.println("BASE READDED, "+parent);
		}*/
		
		SpatialPartition[] children = parent.getChildren();
		for(int i = 0; i < children.length; i++)
		{
			elements.get(parent).addAll(elements.get(children[i]));
			for(Boundable b: elements.get(children[i]))
			{
				m.get(b).remove(children[i]);
				m.get(b).add(parent);
			}
			elements.remove(children[i]);
		}
		parent.collapse();
	}
	/**
	 * tests to see if the passed boundable item intersects anything else in the partition,
	 * the passed item must be already sorted into a partition
	 * @param b an already sorted (ie already added to the partition) boundable object
	 * @return returns a set of the boundable objects that intersect the passed object, will return
	 * an empty set if the passed boundable object has not already been added to the partition
	 */
	public HashSet<Boundable> sortedIntersects(Boundable b)
	{
		HashSet<Boundable> intersections = new HashSet<Boundable>();
		if(m.get(b) != null)
		{
			Iterator<SpatialPartition> spi = m.get(b).iterator();
			while(spi.hasNext())
			{
				HashSet<Boundable> hs = elements.get(spi.next());
				for(Boundable temp: hs)
				{
					if(!intersections.contains(temp) && temp != b && temp.getBounds().intersects(b.getBounds()))
					{
						intersections.add(temp);
					}
				}
			}
		}
		return intersections;
	}
	/**
	 * performs an intersection test on the passed object, the object should not be contained
	 * already in the partition manager, if it is the sortedIntersects method should be used
	 * instead
	 * @param b
	 * @return returns a set containing objects in the partition that intersect the passed object
	 */
	public HashSet<Boundable> intersects(Boundable b)
	{
		HashSet<Boundable> intersects = new HashSet<Boundable>();
		intersectsHelper(base, b, intersects);
		return intersects;
	}
	/**
	 * recursively tests partitions for intersections then adds the elements of that partition
	 * that intersect with the object passed being tested to the hash set
	 * @param sp
	 * @param b the object being tested for intersections
	 * @param intersects
	 */
	private void intersectsHelper(SpatialPartition sp, Boundable b, HashSet<Boundable> intersects)
	{
		if(sp.intersects(b.getBounds()))
		{
			if(sp.getChildren() != null)
			{
				for(int i = 0; i < sp.getChildren().length; i++)
				{
					intersectsHelper(sp.getChildren()[i], b, intersects);
				}
			}
			else
			{
				for(Boundable temp: elements.get(sp))
				{
					if(temp.getBounds().intersects(b.getBounds()))
					{
						intersects.add(temp);
					}
				}
			}
		}
	}
	/**
	 * quickly checks to determine if the passed object intersects another
	 * boundable object already sorted into the partition, the passed object
	 * must already be sorted into the partition
	 * @param b
	 * @return returns the boundable object that intersects the passed object,
	 * returns null if no intersections exist
	 */
	public Boundable fastSortedIntersects(Boundable b)
	{
		return fastSortedIntersectsHelper(base, b);
	}
	/**
	 * recursively checks quickly to determine if an intersection exists between
	 * the passed boundable object and a boundable object already stored in the partition,
	 * the passed object must already be sorted into the partition
	 * @param sp
	 * @param b
	 * @return returns the boundable object found to be intersecting the passed object,
	 * null if no intersections
	 */
	private Boundable fastSortedIntersectsHelper(SpatialPartition sp, Boundable b)
	{
		if(sp.intersects(b.getBounds()))
		{
			if(sp.getChildren() != null)
			{
				for(int i = 0; i < sp.getChildren().length; i++)
				{
					//if false it must continue because other partitions may have an intersection
					Boundable temp = fastSortedIntersectsHelper(sp.getChildren()[i], b);
					if(temp != null)
					{
						return temp;
					}
				}
			}
			else
			{
				for(Boundable temp: elements.get(sp))
				{
					if(temp.getBounds().intersects(b.getBounds()))
					{
						return temp;
					}
				}
			}
		}
		return null;
	}
	/**
	 * performs an intersection test on the region of space
	 * instead
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return returns a set containing objects in the partition that intersect the passed object
	 */
	public HashSet<Boundable> intersects(double x, double y, double width, double height)
	{
		return intersects(new Rectangle(x, y, width, height));
	}
}
