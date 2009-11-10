package mapEditor.menus.editMenu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import mapEditor.MapEditor;

public class TerrainPlacementDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	public TerrainPlacementDialog(MapEditor me, JFrame owner)
	{
		if(me.getTerrainDirectory() == null)
		{
			JPanel p = new JPanel();
			p.setLayout(new FlowLayout());
			p.add(new JLabel("terrain .class directory root not set"));
			JButton close = new JButton("Close");
			close.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					dispose();
				}
			});
			p.add(close);
			add(p);
		}
		else
		{
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			//p.add(new JLabel("Close out of the terrain selector to refresh the selection choices"), BorderLayout.NORTH);
			ArrayList<File> files = new ArrayList<File>();
			getTerrainFiles(me.getTerrainDirectory(), files, ".class");
			p.add(new JLabel("Terrain Files: "+files.size()), BorderLayout.NORTH);
			p.add(createTerrainSelector(files), BorderLayout.CENTER);
			JButton close = new JButton("Close");
			close.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					dispose();
				}
			});
			p.add(close, BorderLayout.SOUTH);
			add(p);
		}
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
	/**
	 * creates the large scrollable selector
	 * @param files
	 * @return
	 */
	public JScrollPane createTerrainSelector(ArrayList<File> files)
	{
		Iterator<File> i = files.iterator();
		JPanel all = new JPanel();
		all.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		while(i.hasNext())
		{
			File f = i.next();
			JPanel p = new JPanel();
			p.setBorder(BorderFactory.createEtchedBorder());
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			
			p.add(new JLabel(f.getName()));
			
			p.add(new JLabel("Location:"));
			p.add(new JLabel(f.getAbsolutePath()));

			JButton select = new JButton("Select");
			//p.add(select);
			
			JPanel p2 = new JPanel();
			p2.setLayout(new FlowLayout());
			p2.add(select);
			p2.add(p);
			
			c.gridy++;
			all.add(p2, c);
		}
		JScrollPane sp = new JScrollPane(all);
		sp.setPreferredSize(new Dimension(sp.getPreferredSize().width, 500));
		return sp;
	}
	public static void asd(){}
	/**
	 * sifts through the terrain directory and finds all the terrain files
	 * @return
	 */
	private void getTerrainFiles(File f, ArrayList<File> files, String ending)
	{
		if(f.isDirectory())
		{
			File[] temp = f.listFiles();
			for(int i = 0; i < temp.length; i++)
			{
				getTerrainFiles(temp[i], files, ending);
			}
		}
		else if(f.getAbsolutePath().endsWith(ending) && !f.getAbsolutePath().contains("$"))
		{
			files.add(f);
		}
	}
}
