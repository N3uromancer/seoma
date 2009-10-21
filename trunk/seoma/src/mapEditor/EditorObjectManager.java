package mapEditor;
import java.awt.Graphics;
import java.util.ArrayList;

import util.Camera;
import mapEditor.editorObject.EditorObject;

public class EditorObjectManager
{
	private ArrayList<EditorObject> objects = new ArrayList<EditorObject>();
	Camera c;
	
	public EditorObjectManager(Camera c)
	{
		this.c = c;
	}
	
	public void addObject(EditorObject eo)
	{
		objects.add(eo);
	}
	
	//no super draw method yet implemented
	public void drawObjects(Graphics g)
	{
		for(int i=0; i<objects.size(); i++)
		{
			EditorObject eo = objects.get(i);
			if (c.isOnScreen(eo.getBounds()))
				eo.draw(g);
		}
	}
}
