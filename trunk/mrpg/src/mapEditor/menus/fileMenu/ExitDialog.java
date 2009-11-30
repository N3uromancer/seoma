package mapEditor.menus.fileMenu;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ExitDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	public ExitDialog(JFrame owner)
	{
		super(owner, "Exit Program?", true);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(new JLabel("Are you sure you want to exit?"));
		JPanel decision = new JPanel();
		decision.setLayout(new FlowLayout());
		JButton exit = new JButton("Exit");
		JButton cancel = new JButton("Cancel");
		decision.add(exit);
		decision.add(cancel);
		p.add(decision);
		add(p);
		
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
}
