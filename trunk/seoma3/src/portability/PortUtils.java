package portability;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class PortUtils {
	public static String[] getOSInformation()
	{
		String[] ret = new String[3];
		ret[0] = System.getProperty("os.name");
		ret[1] = System.getProperty("os.version");
		ret[2] = System.getProperty("os.arch");
		return ret;
	}
	
	public static void prepareNativeLibraries(String rootPath)
	{
		String[] OSInfo = getOSInformation();
		String relativeLibPath = "";
		
		System.out.println("OS Information: "+OSInfo[0]+ " "+OSInfo[1]+" "+OSInfo[2]);
		
		if (OSInfo[0].contains("Linux"))
			relativeLibPath += "linux";
		else if (OSInfo[0].contains("Windows"))
			relativeLibPath += "windows";
		else if (OSInfo[0].contains("Mac OS X"))
			relativeLibPath += "osx";
		else
		{
			System.out.println("Unknown OS: "+OSInfo[0]);
			System.exit(0);
		}
		
		relativeLibPath += '-';
		
		if (OSInfo[0].contains("Mac OS X"))
			relativeLibPath += "universal";
		else if (OSInfo[2].equals("x86") ||
			     OSInfo[2].equals("i686") ||
			     OSInfo[2].equals("i386"))
			relativeLibPath += "x86";
		else if (OSInfo[2].equals("x64") ||
				 OSInfo[2].equals("amd64"))
			relativeLibPath += "amd64";
		else
		{
			System.out.println("Unknown arch: "+OSInfo[2]);
			System.exit(0);
		}
		
		if (rootPath.length() != 0 && rootPath.charAt(rootPath.length()-1) != File.separatorChar)
			rootPath += File.separatorChar;
		
		File dir = new File(rootPath + relativeLibPath);
		if (!dir.exists())
		{
			System.out.println("Missing lib directory: "+ rootPath + relativeLibPath);
			System.exit(0);
		}
		
		String destPath = System.getProperty("java.library.path");
		if (destPath.indexOf(File.pathSeparatorChar) != -1)
			destPath = destPath.substring(destPath.indexOf(File.pathSeparatorChar)+1);
		
		if (destPath.charAt(destPath.length()-1) != File.separatorChar)
			destPath += File.separatorChar;
			
		File[] files = dir.listFiles();
		for (File lib : files)
		{
			try {
				FileInputStream fIn = new FileInputStream(lib);
				File dest = new File(destPath + lib.getName());
				if (dest.exists())
					dest.delete();
				FileOutputStream fOut = new FileOutputStream(dest);
				
				byte[] buffer = new byte[fIn.available()];
				
				fIn.read(buffer);
				
				fOut.write(buffer);
			} catch (FileNotFoundException e) {
				/* This will never happen */
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		System.out.println("Copying native libraries: "+rootPath + relativeLibPath + " -> " + destPath);
	}
	
	public static boolean runningFromJar()
	{
		String className = PortUtils.class.getName().replace('.', '/');
		String classJar = PortUtils.class.getResource('/' + className + ".class").toString();
		return classJar.startsWith("jar:");
	}
}
