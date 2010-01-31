package io;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Unit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import ai.AI;

public class SimpleClassLoader extends ClassLoader {
	ArrayList<Class> loadedClasses;
	
	public SimpleClassLoader()
	{
		loadedClasses = new ArrayList<Class>();
	}
	
	private String getClassName(String s)
	{
		String name = s.substring(0, s.lastIndexOf("."));
		
		/* Strip the hash code */
		for (int i = 1; i < s.length(); i++)
		{
			try {
				Integer.parseInt(name.substring(i-1, i));
			} catch (NumberFormatException e) {
				name = name.substring(i-1);
				break;
			}
		}
		
		/* Strip the trailing numbers */
		for (int i = name.length()-1; i > 0; i--)
		{
			try {
				Integer.parseInt(name.substring(i, i+1));
			} catch (NumberFormatException e) {
				name = name.substring(0, i+1);
				break;
			}
		}
		
		System.out.println("Class name: "+s);
		return name;
	}
	
	private Class findClassInList(String className)
	{
		for (Class c: loadedClasses)
		{
			if (c.getName().substring(c.getName().lastIndexOf(".")+1).equalsIgnoreCase(className))
				return c;
		}
		
		return null;
	}
	
	public Class loadClassFromFile(File f) throws IOException
	{
		byte[] fileData;
		FileInputStream fIn = new FileInputStream(f);
		String className = getClassName(f.getName());
		Class c = findClassInList(className);
		
		if (c != null)
			return c;
		
		/* FIXME: We probably shouldn't be using available() like this */
		fileData = new byte[fIn.available()];
		
		fIn.read(fileData);
		
		System.out.println("ClassLoader: Loading "+className);
		try {
			c = defineClass(/* className */ null, fileData, 0, fileData.length);
		} catch (Exception e) {
			c = findClassInList(className);
		}
		
		loadedClasses.add(c);
		return c;
	}
	
	public Object constructObjectFromClass(Class c, Class[] argTypes, Object[] args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
	{
		Constructor<? extends Object> con = c.getConstructor(argTypes);
		System.out.println("Created object: "+con.newInstance(args));
		return con.newInstance(args);
	}
}
