package world.attack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import world.World;
import world.item.weapon.Weapon;
import world.modifier.NetworkUpdateable;
import world.modifier.ObjectType;
import world.unit.Unit;

public class MeleeAttack extends NetworkUpdateable
{
	private Unit u;
	private Weapon w;
	private byte direction;
	
	public MeleeAttack(boolean isGhost, short id, Unit u, Weapon w, byte direction)
	{
		super(isGhost, id, ObjectType.attack, 0, false);
		
		this.u = u;
		this.w = w;
		this.direction = direction;
	}
	public byte[] getState()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.write(getID);
		}
		catch(IOException e){}
		return baos.toByteArray();
	}
	public void loadState(byte[] b)
	{
		
	}
	public void simulate(World w, double tdiff)
	{
		//swing sword here
	}
	public void update(World w, double tdiff)
	{
		//swing sword here
		//damage hit units here
	}
	public double[] getLocation()
	{
		return u.getLocation();
	}
	public void setLocation(double[] l)
	{
		
	}
	@Override
	public byte[] getInitialState() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void loadInitialState(byte[] b) {
		// TODO Auto-generated method stub
		
	}
}
