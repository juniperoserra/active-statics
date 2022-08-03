package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */
import java.awt.*;
import java.awt.image.*;

public class TImage extends GraphicEntity implements ImageObserver {

  private Image image;
  public G g;
  public float x, y;
  public int  width, height;

  public TImage(String fileName, G gIn) {
    g = gIn;
    image = g.applet.getImage(g.applet.getDocumentBase(), fileName);
    image.getWidth(this);
    image.getHeight(this);
  }

  public void draw(Graphics graphics) {
    if (mInvisible)
      return;

    graphics.drawImage(image, (int)x, (int)y, this);

  }

  public boolean hit(Point p) {
    if (p.x >= x && p.x <= (x+width) &&
        p.y >= y && p.y <= (y+height)) {
      return true;
    }
    return false;
  }

// Image observer function
  public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
    if ((infoflags & ImageObserver.WIDTH) != 0) {
      this.width = width;
    }
    if ((infoflags & ImageObserver.HEIGHT) != 0) {
      this.height = height;
    }
    if ((infoflags & ImageObserver.ALLBITS) != 0) {
      g.applet.repaint();
    }
    return true;
  }
}