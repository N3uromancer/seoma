package display.screen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

/**
 * a button that displays an image when painted
 * @author Jack
 *
 */
public class ImageButton extends JButton
{
	private static final long serialVersionUID = 1L;
	BufferedImage bi;
	
	public ImageButton(File f)
	{
		try
		{
			bi = ImageIO.read(f);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void paintComponent(Graphics g)
	{
		if(bi != null)
		{
			g.drawImage(bi, 0, 0, null);
		}
		else
		{
			g.setColor(Color.white);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}
