package world.effect;

import world.modifier.Temporary;
import world.modifier.Updateable;

/**
 * an updateable object that is removed after so much time
 * @author Secondary
 *
 */
public abstract class Effect implements Updateable, Temporary
{
	private boolean isDead = false;
	private double maxTime;
	private double time = 0;
	
	public Effect(double maxTime)
	{
		this.maxTime = maxTime;
	}
	public void update(double tdiff)
	{
		//System.out.println("effect updated");
		time+=tdiff;
		if(time >= maxTime)
		{
			setDead();
		}
		else
		{
			updateEffect(tdiff);
		}
	}
	public abstract void updateEffect(double tdiff);
	public boolean isDead()
	{
		//return false;
		return isDead;
	}
	public void setDead()
	{
		isDead = true;
	}
}
