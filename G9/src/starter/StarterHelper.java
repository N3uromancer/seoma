package starter;

import world.owner.Owner;

public class StarterHelper
{
	public static double[] getNextColor(double[] lastColor)
	{
		double[] nextColor = new double[lastColor.length];
		String binaryString = "";
		byte byteVal;
		
		/* Create a binary string from the last color */
		for (int i = 0; i < lastColor.length; i++)
			binaryString += (int)lastColor[i];
		
		/* Convert the binary string into an byte */
		byteVal = Byte.parseByte(binaryString, 2);
		
		/* Make sure we don't go out-of-bounds later */
		if (byteVal == Byte.MAX_VALUE)
			throw new IllegalArgumentException("No new colors left");
		
		/* Increment the byte to get the next binary string */
		byteVal++;
		
		/* Get the new binary string */
		binaryString = Integer.toBinaryString(byteVal);
		
		/* Pad it to the correct length */
		while (binaryString.length() < nextColor.length)
			binaryString = "0" + binaryString;
		
		/* Initialize the new color array with the binary string */
		for (int i = 0; i < nextColor.length; i++)
			nextColor[i] = Byte.parseByte(binaryString.charAt(i)+"");
		
		return nextColor;
	}
	public static Owner[] getOwners(int ct)
	{
		Owner[] o = new Owner[ct];
		double[] c = new double[] {0, 0, 1};
		
		for (byte i = 0; i < o.length; i++)
		{
			o[i] = new Owner(i, "Player "+i, c);
			c = getNextColor(c);
		}
		
		return o;
	}
}
