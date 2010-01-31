package world.action.actions;

import javax.media.opengl.GL;

import utilities.MathUtil;
import world.action.Action;
import world.unit.Unit;

/**
 * represents a single move action, ex a unit moves from one point to another
 * @author Jack
 *
 */
public final class Move extends Action
{
	private Unit u;
	private double tx;
	private double ty;
	
	/**
	 * creates a new move action
	 * @param u the unit being moved
	 * @param tx the target x location
	 * @param ty the target y location
	 */
	public Move(Unit u, double tx, double ty)
	{
		super("move");
		this.u = u;
		this.tx = tx;
		this.ty = ty;
	}
	public void cancelAction(){}
	public boolean performAction(double tdiff)
	{
		double[] s = u.getLocation();
		double movement = u.getMovement()*tdiff;
		if(MathUtil.distance(s[0], s[1], tx, ty) <= movement)
		{
			u.setLocation(tx, ty);
			return true;
		}
		/*System.out.println("x="+s[0]+", y="+s[1]+", tx="+tx+", ty="+ty);
		System.out.println("d="+distance(s[0], s[1], tx, ty)+", m="+(movement*tdiff));
		System.out.println("------------------------");*/
		double[] ab = {s[0]-tx, s[1]-ty};
		double distance = movement*movement;
		double lambda = (ab[0]*ab[0])+(ab[1]*ab[1]);
		
		lambda = -Math.sqrt(distance / lambda);
		//lambda = distance / lambda;
		
		double[] l = {s[0]+ab[0]*lambda, s[1]+ab[1]*lambda};
		u.setLocation(l[0], l[1]);
		//System.out.println("updating unit position, l="+lambda+", x="+l[0]+", y="+l[1]);
		return false;
	}
	public void drawAction(GL gl)
	{
		gl.glLineWidth(1);
		gl.glColor4d(1, 1, 1, .4);
		double d = 1; //depth
		double[] s = u.getLocation();
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(s[0], s[1], d);
		gl.glVertex3d(tx, ty, d);
		gl.glEnd();
	}
	public void startAction(){}
	public String toString()
	{
		return getName()+" to ("+tx+", "+ty+")";
	}
}
