package regionEditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class RegionEditorMenu extends JMenuBar
{
	private static final long serialVersionUID = 1L;
	private RegionModel model;
	private JFrame owner;

	public RegionEditorMenu(JFrame f, RegionModel m)
	{
		model = m;
		owner = f;
		
		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		file.add(exit);
		add(file);
		
		JMenu edit = new JMenu("Edit");
		JMenuItem properties = new JMenuItem("Properties");
		properties.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				PropertiesDialog c = new PropertiesDialog(model);
				c.setLocationRelativeTo(owner);
			}
		});
		edit.add(properties);
		add(edit);
		
		JMenu help = new JMenu("Help");
		JMenuItem controls = new JMenuItem("Controls");
		controls.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				ControlDialog c = new ControlDialog();
				c.setLocationRelativeTo(owner);
			}
		});
		help.add(controls);
		add(help);
	}
}
/**
 * defines a dialog detailing the controls used to manipulate the editor
 * @author Jack
 *
 */
class ControlDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	public ControlDialog()
	{
		setTitle("Controls");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Container cp = getContentPane();
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(new JLabel("w - move camera up"));
		p.add(new JLabel("d - move camera right"));
		p.add(new JLabel("s - move camera down"));
		p.add(new JLabel("a - move camera left"));
		p.add(new JLabel("-----------------------"));
		p.add(new JLabel("r - zoom camera in"));
		p.add(new JLabel("f - zoom camera out"));
		cp.add(p, BorderLayout.CENTER);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		cp.add(close, BorderLayout.SOUTH);
		
		pack();
		setVisible(true);
	}
}
/**
 * defines a dialog for editing region model properties
 * @author Jack
 *
 */
class PropertiesDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private RegionModel model;
	
	private JTextField width = new JTextField(10);
	private JTextField height = new JTextField(10);
	

	public PropertiesDialog(RegionModel m)
	{
		setTitle("Properties");
		model = m;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		Container cp = getContentPane();
		
		width.setText(""+model.getSize()[0]);
		height.setText(""+model.getSize()[1]);
		JPanel dimensions = new JPanel();
		dimensions.setLayout(new BoxLayout(dimensions, BoxLayout.Y_AXIS));
		dimensions.add(new JLabel("Map Dimensions:"));
		dimensions.add(new LabeledComponent("Width:", width));
		dimensions.add(new LabeledComponent("Height:", height));
		cp.add(dimensions, BorderLayout.CENTER);
		
		JButton accept = new JButton("Accept");
		accept.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					int w = Integer.parseInt(width.getText());
					int h = Integer.parseInt(height.getText());
					
					//changes made at the end after input value read correctly
					model.setSize(w, h);
					dispose();
				}
				catch(NumberFormatException a){}
			}
		});
		add(accept, BorderLayout.SOUTH);
		
		pack();
		setVisible(true);
	}
}
class LabeledComponent extends JPanel
{
	private static final long serialVersionUID = 1L;
	public LabeledComponent(String label, JComponent c)
	{
		setLayout(new FlowLayout());
		add(new JLabel(label));
		add(c);
	}
}