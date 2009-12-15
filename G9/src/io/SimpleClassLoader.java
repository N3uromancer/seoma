package io;

import gameEngine.world.owner.Owner;
import gameEngine.world.unit.Unit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SimpleClassLoader extends ClassLoader {	
	public Class loadClassFromFile(File f) throws IOException
	{
		byte[] fileData;
		FileInputStream fIn = new FileInputStream(f);
		String className = f.getName().substring(0, f.getName().lastIndexOf(".")-1);
		
		/* FIXME: We probably shouldn't be using available() like this */
		fileData = new byte[fIn.available()];
		
		fIn.read(fileData);
		
		System.out.println("ClassLoader: Loading "+className);
		return defineClass(className, fileData, 0, fileData.length);
	}
	
	public Object constructObjectFromClass(Class c, Class[] argTypes, Object[] args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
	{
		Constructor<?> con = c.getConstructor(argTypes);
		return con.newInstance(args);
	}
}
