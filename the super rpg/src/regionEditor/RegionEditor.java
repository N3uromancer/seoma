package regionEditor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JPanel;

import world.terrain.Terrain;
import display.Camera;

public final class RegionEditor implements Runnable, KeyListener, MouseListener, MouseMotionListener
{
	private JFrame f;
	private JPanel display;
	private RegionModel model;
	
	//user input
	boolean rightClick;
	double[] lastClick;
	
	//camera objects
	Camera c;
	private HashMap<Character, double[]> actions = new HashMap<Character, double[]>();
	private HashMap<Character, Double> z = new HashMap<Character, Double>(); //zoom key map
	private double zoom = 1;
	private HashSet<Character> down = new HashSet<Character>();
	Semaphore downSem = new Semaphore(1, true);
	
	public RegionEditor()
	{
		model = new RegionModel();
		c = new Camera(new double[]{0, 0}, 0, 0);
		
		double m = 250;
		actions.put('w', new double[]{0, m});
		actions.put('d', new double[]{m, 0});
		actions.put('s', new double[]{0, -m});
		actions.put('a', new double[]{-m, 0});
		z.put('r', 1.1);
		z.put('f', .9);
		
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = e.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();
		
		f = new JFrame(config);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/*f.addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent e)
			{
				if(e.getKeyChar() == KeyEvent.VK_ESCAPE)
				{
					System.exit(0);
				}
			}
		});*/
		f.addKeyListener(this);
		display = new JPanel()
		{
			private static final long serialVersionUID = 1L;
			public void paint(Graphics graphics)
			{
				Graphics2D g = (Graphics2D)graphics;
				g.setColor(model.getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				c.setViewBounds(getWidth(), getHeight());
				g.setTransform(c.getTransform());
				model.drawRegionModel(g, c, getWidth(), getHeight());
			}
		};
		display.setPreferredSize(new Dimension(900, 600));
		display.addMouseListener(this);
		display.addMouseMotionListener(this);
		f.setJMenuBar(new RegionEditorMenu(f, model));
		f.add(display);
		f.pack();
		f.setVisible(true);
		new Thread(this).start();
	}
	public static void main(String[] args)
	{
		new RegionEditor();
	}
	public void run()
	{
		long sleepTime = 30;
		long start = System.currentTimeMillis();
		long tdiff;
		long runTime;
		long runs = 0;
		long totalRunTime = 0;
		for(;;)
		{
			tdiff = System.currentTimeMillis()-start;
			start = System.currentTimeMillis();
			display.repaint();
			try
			{
				downSem.acquire();
				for(Character c: down)
				{
					if(actions.containsKey(c))
					{
						this.c.translate(actions.get(c), tdiff/1000.);
					}
					if(z.containsKey(c))
					{
						zoom*=z.get(c);//*tdiff/1000.;
						this.c.zoom(zoom);
					}
				}
				downSem.release();
				runTime = System.currentTimeMillis()-start;
				totalRunTime+=runTime;
				runs++;
				if(runs%1000 == 0)
				{
					//System.out.println("runs/sec = ["+runs+"] / ["+totalRunTime+" ms] = "+(runs*1000./totalRunTime));
				}
				if(runTime < sleepTime)
				{
					Thread.sleep(sleepTime-runTime);
				}
			}
			catch(InterruptedException e){}
		}
	}
	public void keyPressed(KeyEvent e)
	{
		try
		{
			downSem.acquire();
			down.add(e.getKeyChar());
			downSem.release();
		}
		catch(InterruptedException a){}
	}
	public void keyReleased(KeyEvent e)
	{
		try
		{
			downSem.acquire();
			down.remove(e.getKeyChar());
			downSem.release();
		}
		catch(InterruptedException a){}
	}
	public void keyTyped(KeyEvent arg0){}
	public void mouseClicked(MouseEvent arg0){}
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}
	public void mousePressed(MouseEvent e)
	{
		rightClick = e.getButton() == MouseEvent.BUTTON3;
		lastClick = c.getGameLocation(new int[]{e.getPoint().x, e.getPoint().y});
		//System.out.println(lastClick[0]+", "+lastClick[1]);
		model.setTerrain(lastClick, rightClick? Terrain.grass: Terrain.wall);
	}
	public void mouseReleased(MouseEvent arg0){}
	public void mouseDragged(MouseEvent e)
	{
		double[] temp = c.getGameLocation(new int[]{e.getPoint().x, e.getPoint().y});
		if(!(temp[0] == lastClick[0] && temp[1] == lastClick[1]))
		{
			lastClick = temp;
			//System.out.println(lastClick[0]+", "+lastClick[1]);
			model.setTerrain(lastClick, rightClick? Terrain.grass: Terrain.wall);
		}
	}
	public void mouseMoved(MouseEvent arg0){}
}
