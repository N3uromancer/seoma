package network.server;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ServerDisplay extends JFrame
{
	private static final long serialVersionUID = 1L;
	Server s;
	
	public ServerDisplay(Server server)
	{
		super("MRPG Server");
		this.s = server;
		setLayout(new FlowLayout());
		add(new JLabel("Server Running..."));
		JButton terminate = new JButton("Terminate");
		terminate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				s.closeServer();
			}
		});
		add(terminate);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
}
