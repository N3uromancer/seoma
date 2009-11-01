package utilities;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Iterator;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import utilities.Region;
import utilities.SpatialPartition;
import com.sun.opengl.util.j2d.TextRenderer;

/**
 * draws the game, where the user input listener is registered, actually triggers
 * all user input
 * @author Jack
 *
 */
public class SpatialPartitionDebugDisplay implements GLEventListener, KeyListener, MouseListener
{
	int width; //the width of the view area
	int height;
	
	Font font = new Font("SansSerif", Font.PLAIN, 12);
    TextRenderer tr = new TextRenderer(font, true, false);
    
    SpatialPartition sp = new SpatialPartition(0, 0, 500, 500, 20, 8, 150);

	HashSet<Region> r = new HashSet<Region>();
	HashSet<Region> intersects = new HashSet<Region>();
	
	int intersections = 0; //the number of intersections on the last intersection test
	long intTestTime = 0; //the time it took for the last intersection test
	long totalIntTestTime = 0;
	
	public SpatialPartitionDebugDisplay()
	{
		/*int width = 500;
		int height = 500;
		int bwidth = 50;
		for(int i = 0; i < 0; i++)
		{
			Region temp = new Region(Math.random()*width, Math.random()*height, bwidth, bwidth);
			//System.out.println("testing "+temp);
			sp.addRegion(temp);
			r.add(temp);
			//System.out.println("---------------------------");
		}
		
		//System.out.println("size = "+sp.size());
		//System.out.println("true size = "+sp.trueSize());*/
		
		//System.out.println("size = "+sp.getRegions().size());
		
		/*long start = System.currentTimeMillis();
		HashSet<Region> inter = sp.getIntersections(new Region(20, 20, 60, 60));
		long diff = System.currentTimeMillis()-start;
		System.out.println("intersects = "+inter.size());
		System.out.println("time (ms) = "+diff);*/
	}
	public void display(GLAutoDrawable d)
	{
		GL gl = d.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);


		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, this.width, 0, this.height, -1, 1);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glColor3d(1, 1, 1);
		sp.drawPartition(gl, width, height);
		
		gl.glColor3d(1, 0, 0);
		Iterator<Region> i = r.iterator();
		while(i.hasNext())
		{
			i.next().drawRegion(gl);
		}
		gl.glColor3d(0, 1, 0);
		i = intersects.iterator();
		while(i.hasNext())
		{
			i.next().drawRegion(gl);
		}
		
		int off = 20;
		int x = 600;
		tr.beginRendering(width, height);
		tr.draw("regions = "+r.size(), x, height-off);
		off+=20;
		tr.draw("intersection tests = "+intersects.size(), x, height-off);
		off+=20;
		if(intersects.size() == 0)
		{
			tr.draw("average intersection test time (ms) = 0", x, height-off);
		}
		else
		{
			tr.draw("average intersection test time (ms) = "+(totalIntTestTime*1.0/intersects.size()), x, height-off);
		}
		off+=20;
		tr.draw("-------------------", x, height-off);
		off+=20;
		tr.draw("number of intersects on last test = "+intersections, x, height-off);
		off+=20;
		tr.draw("last intersection test time = "+intTestTime, x, height-off);
		off+=20;
		tr.draw("-------------------", x, height-off);
		off+=20;
		tr.draw("'q' (or 'w') = add region, 'e' (or 'r') = intersection test", x, height-off);
		off+=20;
		tr.endRendering();
	}
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2){}
	public void init(GLAutoDrawable d)
	{
		GL gl = d.getGL();
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glClearColor(0, 0, 0, 0);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}
	public void reshape(GLAutoDrawable d, int arg1, int arg2, int width, int height)
	{
		GL gl = d.getGL();
		gl.glViewport(0, 0, width, height);
		
		/*gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-width/2, width/2, -height/2, height/2, -1, 1);*/

		this.width = width;
		this.height = height;
	}
	public static void main(String[] args)
	{
		SpatialPartitionDebugDisplay gd = new SpatialPartitionDebugDisplay();
		GLFrame glf = new GLFrame(gd);
		glf.getGLCanvas().addKeyListener(gd);
		glf.getGLCanvas().addMouseListener(gd);
	}
	public void keyPressed(KeyEvent arg0){}
	public void keyReleased(KeyEvent arg0){}
	public void keyTyped(KeyEvent e)
	{
		if(e.getKeyChar() == 'q')
		{
			int width = 500;
			int height = 500;
			int bwidth = 50;
			Region temp = new Region(Math.random()*width, Math.random()*height, bwidth, bwidth);
			//System.out.println("testing "+temp);
			sp.addRegion(temp);
			r.add(temp);
		}
		else if(e.getKeyChar() == 'w')
		{
			for(int i = 0; i < 100; i++)
			{
				int width = 500;
				int height = 500;
				int bwidth = 50;
				Region temp = new Region(Math.random()*width, Math.random()*height, bwidth, bwidth);
				//System.out.println("testing "+temp);
				sp.addRegion(temp);
				r.add(temp);
			}
		}
		else if(e.getKeyChar() == 'e')
		{
			int width = 500;
			int height = 500;
			long start = System.currentTimeMillis();
			Region temp = new Region(Math.random()*width, Math.random()*height, 100, 100);
			HashSet<Region> inter = sp.getIntersections(temp);
			long diff = System.currentTimeMillis()-start;
			intersects.add(temp);
			intersections = inter.size();
			intTestTime = diff;
			totalIntTestTime+=diff;
		}
		else if(e.getKeyChar() == 'r')
		{
			for(int i = 0; i < 100; i++)
			{
				int width = 500;
				int height = 500;
				long start = System.currentTimeMillis();
				Region temp = new Region(Math.random()*width, Math.random()*height, 100, 100);
				HashSet<Region> inter = sp.getIntersections(temp);
				long diff = System.currentTimeMillis()-start;
				intersects.add(temp);
				intersections = inter.size();
				intTestTime = diff;
				totalIntTestTime+=diff;
			}
		}
		else if(e.getKeyChar() == 'a')
		{
			int index = (int)(Math.random()*r.size());
			Iterator<Region> i = r.iterator();
			int count = 0;
			while(i.hasNext() && count < index)
			{
				count++;
				i.next();
			}
			Region temp = i.next();
			sp.removeRegion(temp);
			r.remove(temp);
		}
	}
	public void mouseClicked(MouseEvent e)
	{
		if(r.size() > 0)
		{
			double x = e.getPoint().x;
			double y = height-e.getPoint().y;
			//HashSet<Region> temp = sp.getIntersections(new Region(x, y, 1, 1));
			HashSet<Region> temp = sp.getRegions(x, y, 20);
			Iterator<Region> i = temp.iterator();
			if(temp.size() > 0)
			{
				Region reg = i.next();
				r.remove(reg);
				sp.removeRegion(reg);
			}
		}
	}
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}
	public void mousePressed(MouseEvent arg0){}
	public void mouseReleased(MouseEvent arg0){}
}
