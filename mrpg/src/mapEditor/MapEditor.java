package mapEditor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.media.opengl.GL;
import mapEditor.menus.MapEditorMenuBar;
import mapEditor.menus.fileMenu.ExitDialog;
import mapEditor.toolBar.MapEditorToolBar;
import ui.UIFrame;
import ui.display.DisplayManager;
import ui.userIO.UserInputInterpreter;
import utilities.Location;
import utilities.MathUtil;
import utilities.Polygon;

public class MapEditor implements UserInputInterpreter, DisplayManager
{
	/**
	 * represents the keys that cause movement
	 */
	HashMap<Character, double[]> viewControls = new HashMap<Character, double[]>();
	int xt = 0; //x translational amount of the view area
	int yt = 0;
	double zoom = 1;
	
	World w = new World();
	UIFrame uif;
	
	//.class directories
	File terrainDir;
	
	public MapEditor()
	{
		int movement = 20;
		double zoom = .01;
		viewControls.put('w', new double[]{0, movement, 0}); //third index zoom amount
		viewControls.put('d', new double[]{movement, 0, 0});
		viewControls.put('s', new double[]{0, -movement, 0});
		viewControls.put('a', new double[]{-movement, 0, 0});
		viewControls.put('r', new double[]{0, 0, zoom});
		viewControls.put('f', new double[]{0, 0, -zoom});
		
		uif = new UIFrame(this, this);
		uif.setJMenuBar(new MapEditorMenuBar(this, uif, w));
		uif.setTitle("Map Editor Beta");
		uif.validate();
		
		uif.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                new ExitDialog(uif);
            }
        });
	}
	/**
	 * writes the map data to the passed stream, map data includes
	 * locations of the .class directories, as well as things such
	 * as the width and height of the map, etc
	 * @param dos
	 */
	public void writeMapEditorData(DataOutputStream dos) throws IOException
	{
		dos.writeDouble(1); //version
		dos.writeInt(terrainDir.getAbsolutePath().length());
		dos.writeChars(terrainDir.getAbsolutePath());
	}
	public File getTerrainDirectory()
	{
		return terrainDir;
	}
	public void setTerrainDirectory(File terrainDir)
	{
		this.terrainDir = terrainDir;
	}
	/**
	 * creates a rectangular polygon based off the passed parameters
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param width
	 * @return returns a polygon
	 */
	public Polygon calculatePolygon(double x1, double y1, double x2, double y2, double width)
	{
		//System.out.println("points = "+x1+", "+y1+", "+x2+", "+y2);
		double[][] p = new double[4][2]; //the vertices of the rectangle
		double[] normal = MathUtil.normal(x1, y1, x2, y2);
		p[0][0] = -normal[0]*width/2+x1;
		p[0][1] = -normal[1]*width/2+y1;
		p[1][0] = normal[0]*width/2+x1;
		p[1][1] = normal[1]*width/2+y1;
		p[2][0] = normal[0]*width/2+x2;
		p[2][1] = normal[1]*width/2+y2;
		p[3][0] = -normal[0]*width/2+x2;
		p[3][1] = -normal[1]*width/2+y2;
		
		Location[] vertices = new Location[p.length];
		for(int i = 0; i < vertices.length; i++)
		{
			vertices[i] = new Location(p[i][0], p[i][1]);
			//System.out.println(vertices[i]);
		}
		//System.out.println("bounds = "+Polygon.determineBoundingRegion(vertices));
		//System.out.println("--------------------");
		return new Polygon(Polygon.determineBoundingRegion(vertices), vertices);
	}
	public static void main(String[] args)
	{
		new MapEditor();
	}
	public void keyAction(char c, boolean pressed)
	{
		if(pressed)
		{
			xt+=viewControls.get(c)[0];
			yt+=viewControls.get(c)[1];
			zoom+=viewControls.get(c)[2];
		}
	}
	public void mouseAction(int x, int y, boolean pressed, boolean rightClick)
	{
		
	}
	public void mouseMoveAction(int x, int y, boolean dragged, boolean rightClick)
	{
		
	}
	public void display(GL gl, int viewWidth, int viewHeight)
	{
		gl.glTranslated(-xt, -yt, 0);
		gl.glScaled(zoom, zoom, 0);
		w.drawWorld(gl, xt, yt, viewWidth, viewHeight);
	}
	@Override
	public void keyTyped(char c) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseClickAction(int x, int y, boolean rightClick) {
		// TODO Auto-generated method stub
		
	}
}
