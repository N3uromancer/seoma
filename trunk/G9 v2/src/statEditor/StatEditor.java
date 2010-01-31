package statEditor;

import javax.swing.*;

/**
 * edits the stats for various game features
 * @author Jack
 *
 */
public class StatEditor extends JFrame
{
	private static final long serialVersionUID = 1L;

	public StatEditor()
	{
		super("G9 Stat Editor");
		
		JTabbedPane tp = new JTabbedPane();
		tp.addTab("Units", new UnitEditorPanel());
		tp.addTab("Weapons", new WeaponEditorPanel());
		
		add(tp);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	public static void main(String[] args)
	{
		new StatEditor();
	}
}
