package mapEditor;

import java.awt.*;

import javax.swing.*;
import util.Camera;



public class MapEditor extends JFrame  implements Runnable
{
	JFrame jf;
	Graphics g;
	EditorDisplay ed;
	InputHandler ih;
	Camera c;
	int editorWidth = 800;
	int editorHeight = 600;
	
	public MapEditor()
	{
		c = new Camera(editorWidth, editorHeight);
		ed = new EditorDisplay(this, c);
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
