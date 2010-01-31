package statEditor;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * panel for editing weapons
 * @author Jack
 *
 */
public class WeaponEditorPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	public WeaponEditorPanel()
	{
		Object[] columnNames = {"name", "range", "shot"};
		Object[][] data = new Object[50][columnNames.length];
		File wdir = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"data"+
				System.getProperty("file.separator")+"weapon data"+System.getProperty("file.separator"));
		loadWeaponData(wdir, data);
		JTable t = new JTable(data, columnNames);
		JScrollPane sp = new JScrollPane(t);
		sp.setPreferredSize(new Dimension(700, 500));
		add(sp);
	}
	private void loadWeaponData(File f, Object[][] data)
	{
		
	}
	public void saveData(Object[][] data)
	{
		
	}
}
