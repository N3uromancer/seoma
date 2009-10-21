package util;

import java.awt.Point;
import java.awt.Rectangle;

/* Camera Class which will need to be modified eventually for OpenGL
 * currently it is just for the map editor
*/
public class Camera
{
	private int xoffset = 0;
    private int yoffset = 0;
    private int width; 
    private int height;

    public Camera(int width, int height)
    {
            this.width = width;
            this.height = height;
    }

    public int getxoffset()
    {
            return xoffset;
    }

    public int getyoffset()
    {
            return yoffset;
    }
    
    public int getWidth()
    {
            return width;
    }
  
    public int getHeight()
    {
            return height;
    }
    
    public void setWidth(int newWidth)
    {
            width = newWidth;
    }
    
    public void setHeight(int newHeight)
    {
            height = newHeight;
    }
    
    public boolean isOnScreen(Rectangle r)
    {
            Rectangle sbounds = new Rectangle(xoffset, yoffset, width, height);
            if(sbounds.intersects(r) || sbounds.contains(r))
            {
                    return true;
            }
            else
            {
                    return false;
            }
    }
    
    public Point getScreenLocation(Point p)
    {
            return new Point(p.x-xoffset, p.y-yoffset);
    }
}
