package geomUtil;

import java.util.HashSet;

import geom.Rectangle;

class SpatialPartition extends Rectangle
{
	SpatialPartition parent; //null if root
	SpatialPartition[] r;
	
	public SpatialPartition(SpatialPartition parent, double x, double y, double width, double height)
	{
		super(x, y, width, height);
		this.parent = parent;
	}
	/**
	 * returns a set of the partitions of the passed rectangle is to be inserted into
	 * @param rect
	 * @return
	 */
	public HashSet<SpatialPartition> add(Rectangle rect)
	{
		HashSet<SpatialPartition> partitions = new HashSet<SpatialPartition>();
		addHelper(rect, partitions);
		return partitions;
	}
	/**
	 * collpases the partition by nulling the children sub partitions
	 */
	public void collapse()
	{
		r = null;
	}
	private HashSet<SpatialPartition> addHelper(Rectangle rect, HashSet<SpatialPartition> sp)
	{
		if(intersects(rect))
		{
			if(r != null)
			{
				/*for(int i = 0; i < r.length; i++)
				{
					sp.addAll(r[i].addHelper(rect, sp));
				}*/
				for(int i = 0; i < r.length; i++)
				{
					r[i].addHelper(rect, sp);
				}
			}
			else
			{
				sp.add(this);
			}
		}
		return sp;
	}
	/**
	 * splits the partition into 4 smaller partitions
	 * @return returns the newly created sub partitions
	 */
	public SpatialPartition[] split()
	{
		r = new SpatialPartition[4];
		r[0] = new SpatialPartition(this, x, y, width/2, height/2);
		r[1] = new SpatialPartition(this, x+width/2, y, width/2, height/2);
		r[2] = new SpatialPartition(this, x+width/2, y+height/2, width/2, height/2);
		r[3] = new SpatialPartition(this, x, y+height/2, width/2, height/2);
		return r;
	}
	/**
	 * gets the parent partition
	 * @return returns the parent partition, null if root
	 */
	public SpatialPartition getParent()
	{
		return parent;
	}
	/**
	 * getes the child of the partition
	 * @return returns the children of the partition, null if no children
	 */
	public SpatialPartition[] getChildren()
	{
		return r;
	}
}