package mapEditor.editorObject;

import java.awt.Graphics;

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
		g.fillRect(x, y, width, height);
	}
}
