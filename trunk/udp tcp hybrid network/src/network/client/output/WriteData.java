package network.client.output;

import world.GameObject;

class WriteData implements Comparable<WriteData>
{
	GameObject o;
	/**
	 * the higher the priority the closer it is to the head of the queue,
	 * when the priority changes it must be removed from the queue and readded
	 */
	int priority = 0;
	byte[] data;
	
	public WriteData(GameObject o, byte[] data)
	{
		this.o = o;
		this.data = data;
	}
	public int getPriority()
	{
		return priority;
	}
	public void setPriority(int setter)
	{
		priority = setter;
	}
	public GameObject getObject()
	{
		return o;
	}
	public int compareTo(WriteData wd)
	{
		if(priority == wd.getPriority())
		{
			return 0;
		}
		else if(priority > wd.getPriority())
		{
			/*
			 * priority is greater but returns the less than value because
			 * the priority queue sorts with the "lesser" values first
			 */
			return -1;
		}
		else
		{
			return 1;
		}
	}
	public byte[] getData()
	{
		return data;
	}
}
