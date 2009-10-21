package mapEditor;

import java.awt.*;

import javax.swing.*;

import mapEditor.editorObject.EditorObject;
import util.Camera;

/*beginnings of the map editor
 * TODO
 * -loading objects from files as EditorObjects (unit editor will be separate, EditorObject just for display)
 * -placing/removing objects
 * -saving/loading map files
 */
public class MapEditor extends JFrame  implements Runnable
{
	JFrame jf;
	Graphics g;
	EditorDisplay ed;
	InputHandler ih;
	Camera c;
	EditorObjectManager eom;
	int editorWidth = 800;
	int editorHeight = 600;
	
	public MapEditor()
	{
		c = new Camera(editorWidth, editorHeight);
		eom = new EditorObjectManager(c);
		ed = new EditorDisplay(c, this, eom);
		ih = new InputHandler();
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(ed, BorderLayout.CENTER);

		jf = new JFrame("Map Editor");
		jf.setContentPane(content);
		jf.addKeyListener(ih);
		jf.setSize(editorWidth, editorHeight);
		jf.setLocation(100, 100);
		jf.setVisible(true);
		
		g = jf.getGraphics();
		
		new Thread(this).start();
	}
	
	public static void main(String[] args)
	{
		new MapEditor();
	}
	
	public void run()
	{
		eom.addObject(new EditorObject(50,50,50,50));
		for (;;)
		{
			jf.repaint();
			try
			{
				Thread.sleep(20);
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
