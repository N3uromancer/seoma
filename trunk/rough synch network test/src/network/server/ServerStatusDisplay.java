package network.server;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * a simple status display for the server
 * @author Jack
 *
 */
public class ServerStatusDisplay extends JFrame implements Runnable
{
	private static final long serialVersionUID = 1L;
	/**
	 * the text area where the status updates are displpayed
	 */
	JTextArea ta;
	
	public ServerStatusDisplay()
	{
		super("Server Status Display");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		ta = new JTextArea(20, 50);
		JScrollPane sp = new JScrollPane(ta, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		p.add(sp, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		add(p);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		PipedOutputStream pos = new PipedOutputStream();
		PrintStream ps = new PrintStream(pos);
		System.setOut(ps);
		try
		{
			PipedInputStream pis = new PipedInputStream(pos);
			System.setIn(pis);
		}
		catch(IOException e)
		{
			ta.append("error! io exception in setting up piped streams");
		}
		
		System.out.println("--- exit window to terminate server ---");
		
		new Thread(this).start();
	}
	public void run()
	{
		Scanner s = new Scanner(System.in);
		
		while(s.hasNextLine())
		{
			ta.append(s.nextLine()+"\n");
		}
	}
}
