package world.modifier;

import java.io.DataOutputStream;
import java.nio.ByteBuffer;


/**
 * represents a game object, everything in the game must be an
 * instance of this object
 * @author Jack
 *
 */
public abstract class GameObject
{
	/**
	 * writes the objects state to the output stream
	 */
	public abstract void writeState(DataOutputStream dos);
	/**
	 * reads and updates the game object's state from the byte buffer
	 * @param buff
	 */
	public abstract void readState(ByteBuffer buff);
}
