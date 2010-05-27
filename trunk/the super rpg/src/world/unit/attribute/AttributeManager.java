package world.unit.attribute;

import java.util.HashMap;

/**
 * manages a set of attributes
 * @author Jack
 *
 */
public final class AttributeManager
{
	private HashMap<Attribute, Double> a = new HashMap<Attribute, Double>();
	
	public double getAttribute(Attribute attribute)
	{
		return a.get(attribute);
	}
	public void setAttribute(Attribute attribute, double value)
	{
		a.put(attribute, value);
	}
}
