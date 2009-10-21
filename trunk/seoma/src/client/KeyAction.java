package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashSet;
import com.sun.sgs.client.ClientChannel;

public class KeyAction implements KeyListener
{
	HashSet<Character> down = new HashSet<Character>(); //the pressed keys
	ClientChannel c;
	byte owner;
	
	public KeyAction(ClientChannel c, byte owner)
	{
		this.c = c;
		this.owner = owner;
	}
	public void keyPressed(KeyEvent e)
	{
		//System.out.println("key pressed");
		if(!down.contains(e.getKeyChar()))
		{
			down.add(e.getKeyChar());
			try
			{

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				dos.writeByte(BoxClient.sendInput);
				dos.writeByte(owner);
				dos.writeChar(e.getKeyChar());
				dos.writeByte(1);
				
				c.send(ByteBuffer.wrap(baos.toByteArray()));
			}
			catch(IOException a){}
		}
	}
	public void keyReleased(KeyEvent e)
	{
		down.remove(e.getKeyChar());
	}
	public void keyTyped(KeyEvent e){}
}
