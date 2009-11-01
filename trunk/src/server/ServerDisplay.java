package server;

import javax.swing.*;

/**
 * displays various stats about the server
 * 
 * displays:
 * -number of connected clients (by channel)
 * -output
 * -current status
 * 
 * @author Jack
 *
 */
public class ServerDisplay extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	JTextArea log = new JTextArea(); //lists the various status changes of the server
	JLabel status = new JLabel();
	
	public ServerDisplay()
	{
		
	}
	public void setStatus(String s)
	{
		status.setText(s);
		log.append(s+"\n");
	}
}
