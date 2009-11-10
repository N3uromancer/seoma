package mapEditor.menus.propertiesMenu;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import mapEditor.MapEditor;

/**
 * sets the source settings, where the .class files used in the game are located
 * @author Jack
 *
 */
public final class SourceSettings extends JDialog
{
	private static final long serialVersionUID = 1L;

	public SourceSettings(MapEditor me, JFrame owner)
	{
		super(owner, "Source Settings", true);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		//p.add(new JLabel("Set the directories containing the .class files for the map."));
		p.add(createSelector(owner, me, "Terrain", me.getTerrainDirectory()));
		
		JButton done = new JButton("Done");
		done.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		p.add(done);
		
		add(p);
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
	/**
	 * creates a new jpanel containing a button that starts
	 * a file selector and a jlabel that tells the directory
	 * of the selected file
	 * @param type the name of the button that starts the selector
	 * @param file
	 * @return
	 */
	public JPanel createSelector(JFrame frame, MapEditor editor, String type, File file)
	{
		final File f = file;
		final JFrame owner = frame;
		final MapEditor me = editor;
		
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		
		JButton b = new JButton(type);
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
				if(f != null)
				{
					fc = new JFileChooser(f);
				}
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.showDialog(owner, "Select Directory");
				me.setTerrainDirectory(fc.getSelectedFile());
				dispose();
			}
		});
		
		p.add(b);
		if(f != null)
		{
			p.add(new JLabel(f.getAbsolutePath()));
		}
		else
		{
			p.add(new JLabel("Undefined"));
		}
		return p;
	}
}