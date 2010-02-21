package network.server;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Server
{
	ServerSocket ss;
	/**
	 * the thread that accepts connections to the server
	 */
	ServerAcceptorThread sat;
	
	JTextField port = new JTextField(15);
	Server ref;
	
	public Server()
	{
		createUI();
		ref = this;
	}
	/**
	 * sets up the simple server ui
	 */
	private void createUI()
	{
		final JFrame f = new JFrame("Server");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		p.add(port);
		JButton host = new JButton("Host");
		p.add(host);
		f.add(p);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		
		host.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					int p = Integer.parseInt(port.getText());
					f.dispose();
					new ServerStatusDisplay();
					
					try
					{
						ss = new ServerSocket(p);
						System.out.println("server socket created");
						System.out.println("port = "+p);
						System.out.println("Host Name: "+ss.getInetAddress().getHostName());
						System.out.println("Host IP: "+ss.getInetAddress().getHostAddress());
						
						System.out.println("starting connection acceptor thread...");
						sat = new ServerAcceptorThread(ss, ref, 10000);
						Thread t = new Thread(sat);
						t.setPriority(Thread.MIN_PRIORITY);
						System.out.println("done");
						System.out.println("===============================================");
						t.start();
						
						//new Thread(new UpdateThread(sat)).start();
					}
					catch(IOException a)
					{
						System.out.println("Error: "+a.getMessage());
					}
				}
				catch(NumberFormatException a)
				{
					final JDialog d = new JDialog(f, "Error", true);
					d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					JPanel p = new JPanel();
					p.setLayout(new FlowLayout());
					JTextArea msg = new JTextArea();
					msg.append(a.getMessage());
					msg.setEditable(false);
					p.add(msg);
					JButton close = new JButton("Close");
					close.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
							d.dispose();
						}
					});
					p.add(close);
					d.add(p);
					d.pack();
					d.setLocationRelativeTo(f);
					d.setVisible(true);
				}
			}
		});
	}
	public static void main(String[] args)
	{
		new Server();
	}
	/**
	 * writes the passed buffer to all connected client sockets
	 * except the one passed
	 * @param buff the byte buffer to be written to the output streams
	 * for each other client
	 * @param s the client socket that sent the data to the server
	 * to be forwarded, the buffer is not written to its stream
	 */
	public void writeToAll(byte[] buff, Socket s)
	{
		try
		{
			sat.getSocketSemaphore().acquire();
			for(Socket socket: sat.getSockets().keySet())
			{
				if(socket != s)
				{
					sat.getSockets().get(socket).add(buff);
				}
			}
			sat.getSocketSemaphore().release();
		}
		catch(InterruptedException e){}
	}
	/**
	 * disconnects the passed socket from the server, terminating all
	 * associated threads
	 * @param s
	 */
	public void disconnect(Socket s)
	{
		try
		{
			sat.getSocketSemaphore().acquire();
			sat.getSockets().get(s).disconnect();
			sat.getSocketSemaphore().release();
		}
		catch(InterruptedException e){}
	}
}
