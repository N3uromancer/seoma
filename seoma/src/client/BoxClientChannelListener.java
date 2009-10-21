package client;

import java.nio.ByteBuffer;

import box.Box;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;

public class BoxClientChannelListener implements ClientChannelListener
{
	BoxClient bc;
	boolean listenerCreated = false;
	
	public BoxClientChannelListener(BoxClient c)
	{
		this.bc = c;
	}
	/**
	 * called when the server removes this listener from the channel
	 */
	public void leftChannel(ClientChannel channel)
	{
		bc.appendOutput("Removed from channel "+channel.getName());
	}
	/**
	 * called when the server receives the message
	 */
	public void receivedMessage(ClientChannel channel, ByteBuffer bb)
	{
		bc.appendOutput("received message, "+channel.getName());
		byte operation = bb.get();
		if(operation == BoxClient.sendInput)
		{
			byte owner = bb.get();
			bc.appendOutput("updating user input, owner = "+owner);
			char c = bb.getChar();
			
			byte bp = bb.get();
			boolean pressed = bp == 1;
			
			bc.getKeyManager().registerKey(owner, c, pressed);
		}
		else if(operation == BoxClient.createNewBox)
		{
			byte owner = bb.get();
			double x = bb.getDouble();
			double y = bb.getDouble();
			bc.appendOutput("creating a new unit, owner="+owner+", ("+x+", "+y+")");
			System.out.println("creating a new unit, owner="+owner+", ("+x+", "+y+")");
			bc.getUnits()[owner] = new Box(x, y);
			bc.getKeyManager().registerNewPlayer(owner);
			if(!listenerCreated)
			{
				bc.addKeyListener(new KeyAction(channel, owner));
			}
		}
		else if(operation == BoxClient.updateGame)
		{
			//System.out.println("updating units");
			//bc.getKeyManager().updateUnits(bc.getUnits(), KeyManager.considerAllInputs);
			bc.getKeyManager().updateUnits(bc.getUnits(), 1);
		}
	}
}
