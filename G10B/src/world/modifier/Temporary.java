package world.modifier;

/**
 * defines an object that can be defined as dead, meaning it should
 * be removed from the world
 * @author Secondary
 *
 */
public interface Temporary extends Updateable
{
	public boolean isDead();
	public void setDead();
}
