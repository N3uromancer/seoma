package mapEditor.mapEditorMenuBar;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mapEditor.Map;

/**
 * deals with the properties of the 
 * @author Jack
 *
 */
public class PropertiesMenu extends Menu
{
	Map m;
	Frame frame;
	
	public PropertiesMenu(Frame f, Map map)
	{
		super("Properties");
		this.m = map;
		frame = f;
		
		MenuItem description = new MenuItem("Description");
		description.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				new DescriptionDialog(frame, m);
			}
		});
		MenuItem mapDimensions = new MenuItem("Dimensions");
		mapDimensions.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				new DimensionDialog(frame, m);
			}
		});
		add(description);
		add(mapDimensions);
	}
}
class DescriptionDialog extends JDialog
{
	JTextField name = new JTextField(15);
	JTextArea description = new JTextArea(8, 30);
	Map m;
	
	public DescriptionDialog(Frame frame, Map map)
	{
		super(frame, "Set Map Description", true);
		m = map;
		name.setText(map.getName());
		description.setText(map.getDescription());
		
		JPanel n = new JPanel();
		n.setLayout(new FlowLayout());
		n.add(new JLabel("Name:"));
		n.add(name);

		description.setLineWrap(true);
		JScrollPane sp = new JScrollPane(description, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel d = new JPanel();
		d.setLayout(new FlowLayout());
		d.add(new JLabel("Description:"));
		d.add(sp);
		
		JPanel dec = new JPanel(); //decision
		dec.setLayout(new FlowLayout());
		JButton accept = new JButton("Accept");
		accept.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				m.setName(name.getText());
				m.setDescription(description.getText());
				dispose();
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		dec.add(accept);
		dec.add(cancel);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(n);
		p.add(d);
		p.add(dec);
		add(p);
		pack();
		
		setLocationRelativeTo(frame);
		setVisible(true);
	}
}
class DimensionDialog extends JDialog
{
	TextField width = new TextField(15);
	TextField height = new TextField(15);
	Map m;
	Frame f;
	
	public DimensionDialog(Frame frame, Map map)
	{
		super(frame, "Set Map Dimensions", true);
		m = map;
		f = frame;
		
		width.setText(""+m.getWidth());
		height.setText(""+m.getHeight());
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout());
		p1.add(new JLabel("Width:"));
		p1.add(width);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout());
		p2.add(new JLabel("Height:"));
		p2.add(height);
		setLayout(new FlowLayout());
		
		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout());
		JButton accept = new JButton("Accept");
		accept.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					int w = Integer.parseInt(width.getText());
					int h = Integer.parseInt(height.getText());
					m.setSize(w, h);
					dispose();
				}
				catch(NumberFormatException a)
				{
					new ErrorDialog(f);
				}
			}
		});
		p3.add(accept);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		p3.add(cancel);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(p1);
		p.add(p2);
		p.add(p3);
		add(p);
		//setSize(210, 145);
		pack();
		setLocationRelativeTo(f);
		setVisible(true);
	}
}
