package mapEditor.mapEditorMenuBar;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ErrorDialog extends JDialog
{
	public ErrorDialog(Frame f)
	{
		super(f, "Error!", true);
		//setSize(240, 80);
		setLayout(new FlowLayout());
		add(new JLabel("Entry contains errors."));
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		add(ok);
		pack();
		setLocationRelativeTo(f);
		setVisible(true);
	}
}
