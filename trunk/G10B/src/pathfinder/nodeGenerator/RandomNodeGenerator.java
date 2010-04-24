package pathfinder.nodeGenerator;

import java.util.HashSet;

import pathfinder.StationaryPathable;
import pathfinder.graph.Graph;
import pathfinder.graph.Node;
import world.modifier.PathObstacle;
import world.modifier.Pathable;

/**
 * a node generator that randomly creates nodes that do not intersect path obstacles
 * @author Secondary
 *
 */
public class RandomNodeGenerator implements NodeGenerator
{
	public void generateNodes(Graph g, HashSet<PathObstacle> obstacles,
			double[] radii, double width, double height, int nodes)
	{
		System.out.print("generating pathing nodes... ");
		long start = System.currentTimeMillis();
		for(int i = 0; i < radii.length; i++)
		{
			for(int a = 0; a < nodes; a++)
			{
				double x = Math.random()*width;
				double y = Math.random()*height;
				Pathable p = new StationaryPathable(new double[]{x, y}, radii[i]);
				
				
				boolean intersects = false;
				for(PathObstacle obstacle: obstacles)
				{
					intersects = obstacle.intersects(p, 0);
					if(intersects)
					{
						break;
					}
				}
				if(!intersects)
				{
					g.addNode(new Node(p.getLocation(), radii[i]));
					//System.out.println(radii[i]);
				}
				else
				{
					a--;
				}
			}
		}
		long diff = System.currentTimeMillis()-start;
		System.out.println("done, generated "+nodes*radii.length+" nodes in "+diff+" ms");
	}
}
