package utilities;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.*;

/**
 * a quick utility program for counting lines
 * @author Jack
 *
 */
public class LineCounter extends JFrame
{
	private static final long serialVersionUID = 1L;
	long lines = -1;
	JLabel l;
	JFrame frame;
	File lastViewedDir;

	public LineCounter()
	{
		super("Line Counter");
		frame = this;
		
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		l = new JLabel("lines: "+lines);
		JButton dirSelect = new JButton("Select Directory");
		dirSelect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
				if(lastViewedDir != null)
				{
					fc = new JFileChooser(lastViewedDir);
				}
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnValue = fc.showDialog(frame, "Select Directory");
				if(returnValue == JFileChooser.APPROVE_OPTION)
				{
					File f = fc.getSelectedFile();
					lastViewedDir = f;
					try
					{
						lines = 0;
						countLines(f);
						//System.out.println("done!");
					}
					catch(IOException a){}
					l.setText("lines: "+lines);
					frame.pack();
				}
			}
		});
		p.add(dirSelect);
		p.add(l);
		add(p);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	private void countLines(File f) throws IOException
	{
		if(f.isDirectory())
		{
			System.out.println("lines: "+lines);
			File[] files = f.listFiles();
			for(int i = 0; i < files.length; i++)
			{
				countLines(files[i]);
			}
		}
		else
		{
			if(f.getName().endsWith(".java"))
			{
				System.out.println("reading "+f.getName());
				Scanner s = new Scanner(f);
				while(s.hasNextLine())
				{
					s.nextLine();
					lines++;
				}
			}
		}
	}
	public static void main(String[] args)
	{
		new LineCounter();
	}
}
