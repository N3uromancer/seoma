package utilities;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.sun.opengl.util.Animator;

/**
 * a convenience class, creates a frame, adds the passed GLEventListener
 * to a canvas, canvas added to the frame
 * @author Jack
 *
 */
public class GLFrame extends Frame
{
	private static final long serialVersionUID = 1L;
	GLCanvas canvas;
	Animator animator;
	
	public GLFrame(GLEventListener glel)
	{
		this(glel, 900, 600);
	}
	public GLFrame(GLEventListener glel, int width, int height)
	{
        canvas = new GLCanvas(new GLCapabilities());
        canvas.addGLEventListener(glel);
        add(canvas);
        setSize(width, height);
        setLocationRelativeTo(null); //centers window in the screen
        
       animator = new Animator(canvas);
       
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
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
