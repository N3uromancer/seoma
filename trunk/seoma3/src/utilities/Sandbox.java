package utilities;

import java.awt.Frame;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.sun.opengl.util.Animator;

/**
 * a simple drawing test class, automatically creates a frame and displays
 * everything, simply extend and fill in the draw method to use it
 * @author Jack
 *
 */
public abstract class Sandbox implements GLEventListener
{
	/**
	 * the width of the view area
	 */
	int width;
	/**
	 * the height of the view area
	 */
	int height;
	
	GLFrame glf;
	
	public Sandbox()
	{
		glf = new GLFrame(this);
	}
	public Sandbox(int width, int height)
	{
		glf = new GLFrame(this, width, height);
	}
	public void display(GLAutoDrawable d)
	{
		GL gl = d.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);


		/*gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, this.width, 0, this.height, -1, 1);*/
		
		draw(gl, width, height);
	}
	/**
	 * called every time the display method for the canvas is called
	 * @param gl
	 * @param width the width of the displayed area
	 * @param height the height of the displayed area
	 */
	public abstract void draw(GL gl, int width, int height);
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2){}
	public void init(GLAutoDrawable d)
	{
		GL gl = d.getGL();
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glClearColor(0, 0, 0, 0);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}
	public void reshape(GLAutoDrawable d, int arg1, int arg2, int width, int height)
	{
		GL gl = d.getGL();
		gl.glViewport(0, 0, width, height);
		this.width = width;
		this.height = height;
	}
	/**
	 * adds the passed key listener to the displayed canvas
	 * @param kl
	 */
	public void addKeyListenerToCanvas(KeyListener kl)
	{
		glf.getGLCanvas().addKeyListener(kl);
	}
}
class GLFrame extends Frame
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