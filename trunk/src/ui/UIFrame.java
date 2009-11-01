package ui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import ui.display.Display;
import ui.display.Displayable;
import ui.userIO.UserInputInterpreter;
import ui.userIO.UserInputListener;

/**
 * the frame for the entire program, generates user input here
 * @author Jack
 *
 */
public class UIFrame extends Frame
{
	private static final long serialVersionUID = 1L;
	Display display = new Display();
	UserInputListener uil = new UserInputListener();
	GLCanvas canvas;
	
	public UIFrame(UserInputInterpreter uii, Displayable d)
	{
        canvas = new GLCanvas(new GLCapabilities());
        canvas.addGLEventListener(display);
        add(canvas);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        //declares all the user input listeners
        canvas.addKeyListener(uil);
        canvas.addMouseListener(uil);
        canvas.addMouseMotionListener(uil);
        
        uil.setViewHeight(canvas.getHeight());
        canvas.addComponentListener(new ComponentAdapter(){
        	public void componentResized(ComponentEvent e)
        	{
        		uil.setViewHeight(canvas.getHeight());
        	}
        });
        
        if(uii != null)
        {
        	uil.setUserInputInterpreter(uii);
        }
        if(d != null)
        {
            display.setDisplayable(d);
        }
        
        final com.sun.opengl.util.Animator animator = new com.sun.opengl.util.Animator(canvas);
        
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                new Thread(new Runnable() {
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        setVisible(true);
        animator.start();
	}
	/**
	 * gets the frame's glcanvas
	 * @return returns a reference to the glcanvas used by
	 * this frame
	 */
	public GLCanvas getGLCanvas()
	{
		return canvas;
	}
}
