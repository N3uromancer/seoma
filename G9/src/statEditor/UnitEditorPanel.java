package statEditor;

import io.UnitDataLoader;
import java.awt.Dimension;
import java.io.*;
import java.util.Iterator;
import javax.swing.*;

/**
 * the panel for editor units
 * @author Jack
 *
 */
public class UnitEditorPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	public UnitEditorPanel()
	{
		Object[] columnNames = {"name", "width", "height", "life", "movement", "weapon", "build time", "cost"};
		Object[][] data = new Object[50][columnNames.length];
		File udir = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"data"+
				System.getProperty("file.separator")+"unit data"+System.getProperty("file.separator"));
		loadUnitData(udir, data);
		JTable t = new JTable(data, columnNames);
		JScrollPane sp = new JScrollPane(t);
		sp.setPreferredSize(new Dimension(700, 500));
		add(sp);
	}
	/**
	 * loads the unit data
	 * @param udir
	 * @throws IOException
	 */
	private void loadUnitData(File udir, Object[] data)
	{
		UnitDataLoader udl = new UnitDataLoader(udir);
		Iterator<String> i = udl.getUnitData().keySet().iterator();
		int index = 0;
		while(i.hasNext())
		{
			data[index] = udl.getUnitData().get(i.next());
			index++;
		}
	}
	public void saveData(Object[][] data)
	{
		
	}
}
