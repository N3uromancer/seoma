package utilities;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.Hashtable;


public class CustomClassLoader extends ClassLoader
{
	private Hashtable classes = new Hashtable();
	private String path;
	
	public CustomClassLoader(String path)
	{
		this.path = path;
	}
    private byte getClassImplFromDataBase(String className)[] {
    	//System.out.println("Fetching the implementation of "+className);
    	//String userDir = System.getProperty("user.dir");
    	String loadFrom = path;
    	//System.out.println("getting implementation at "+loadFrom+className);
    	byte result[];
    	try
    	{
    		//FileInputStream fi = new FileInputStream("store\\"+className+".impl");
    		FileInputStream fi = new FileInputStream(loadFrom+System.getProperty("file.separator")+className);
    	    result = new byte[fi.available()];
    	    fi.read(result);
    	    return result;
    	}
    	catch (Exception e)
    	{
    	    return null;
    	}
    }
    public void setPathStart(String path)
    {
    	this.path = path;
    }
    public static Object constructObject(Class c, Class[] arguments, Object... parameters)
    {
    	/*
    	 * example 1: constructs a new object of class c, the constructor of this object has one argument,
    	 * that argument is a string, in the example "asd" is passed as the parameter to be used in
    	 * constructing the object
    	 * 
    	 * Class[] arguments = new Class[1];
    	 * arguments[0] = new String().getClass();
    	 * Object o = ccl.constructObject(c, arguments, new String("asd"));
    	 * 
    	 * constructs a new object of the given class c, the length of the argument array must
    	 * be the number of parameters that the constructor needs, the actual parameters must
    	 * be listed out in the correct order to be passed to the new instance of the object
    	 * 
    	 * example 2: constructs an object in a manner similar to the previous construction, however,
    	 * the object in this exmaple has two parameters that make up its constructor
    	 * 
    	 * Class[] args = new Class[2];
    	 * args[0] = new String().getClass()
    	 * args[1] = new String().getClass();
    	 * Object o = ccl.constructObject(c, args, new String("asd"), new String("qwe"));
    	 */
    	try
    	{
    		Constructor constructor = c.getConstructor(arguments);
    		return constructor.newInstance(parameters);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return null;
    }
    public Class loadClass(String className) throws ClassNotFoundException
    {
        return (loadClass(className, true));
    }
    protected synchronized Class loadClass(String className, boolean resolveIt)
    	throws ClassNotFoundException {
        Class result;
        byte  classData[];

        //System.out.println("\nload class: "+className);

        //check our local cache of classes
        result = (Class)classes.get(className);
        if (result != null) {
            //System.out.println("returning cached result.");
            return result;
        }

        //check with the primordial class loader
        try {
            result = super.findSystemClass(className);
            //System.out.println("returning system class (in CLASSPATH).");
            return result;
        } catch (ClassNotFoundException e) {
            //System.out.println("not a system class.");
        }

        //try to load it from our repository
        classData = getClassImplFromDataBase(className);
        if (classData == null) {
            throw new ClassNotFoundException();
        }

        //define it (parse the class file)
        result = defineClass(null, classData, 0, classData.length);
        if (result == null) {
            throw new ClassFormatError();
        }

        if (resolveIt) {
            resolveClass(result);
        }

        classes.put(className, result);
        //System.out.println("Returning newly loaded class.");
        return result;
    }

}
