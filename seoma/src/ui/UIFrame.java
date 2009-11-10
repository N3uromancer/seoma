package ui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;
import com.sun.opengl.util.Animator;
import ui.display.Display;
import ui.display.DisplayManager;
import ui.userIO.UserInputInterpreter;
import ui.userIO.UserInputListener;

/**
 * the frame for the entire program, generates user input here
 * @author Jack
 *
 */
public class UIFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	Display display = new Display();
	UserInputListener uil = new UserInputListener();
	GLCanvas canvas;
	Animator animator;
	
	/**
	 * creates a new ui frame, a ui frame includes a built in display
	 * @param uii
	 * @param d the display manager, determines what will be displayed by
	 * the built in display
	 */
	public UIFrame(UserInputInterpreter uii, DisplayManager d)
	{
        canvas = new GLCanvas(new GLCapabilities());
        canvas.addGLEventListener(display);
        add(canvas);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        //declares all the user input listeners
        if(uil != null)
        {
        	canvas.addKeyListener(uil);
            canvas.addMouseListener(uil);
            canvas.addMouseMotionListener(uil);
        }
        
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
        
        animator = new Animator(canvas);
        
        /*addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                new Thread(new Runnable() {
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });*/
        setVisible(true);
        animator.start();
	}
	public Animator getAnimator()
	{
		return animator;
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
