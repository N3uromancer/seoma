package mapEditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

// A class for representing units and objects in the editor
public class EditorObject
{
	protected int x, y, width, height;
	
	public EditorObject(int x, int y, int w, int h)
	{
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	//default unit draw method
	public void draw(Graphics g)
	{
		g.setColor(Color.red);
		g.fillRect(x, y, width, height);
	}

	public Rectangle getBounds()
	{
		return new Rectangle(x, y, width, height);
	}
}
