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

public class TPoint extends GraphicEntity {

  public static final int DEFAULT_SIZE = 10;
  public static final int LABEL_EXTENT_WIDTH = 30;
  public float x, y;
  public boolean mControlPoint = true;
  public float xStart, yStart;
  public float mParamOnSeg;
  private Point mHitPoint = new Point();
  private Point mDragOffset = new Point();

  private Point tempPoint =  new Point();

  public TPoint() {
    mSize = DEFAULT_SIZE;
  }

  public TPoint(float aX, float aY) {
    mSize = DEFAULT_SIZE;
    x = aX;
    xStart = aX;
    y = aY;
    yStart = aY;
  }

  private static void putPixel( Graphics gr, int x, int y ){
      gr.drawLine( x, y, x, y );
  }

  public void drawCircle(Graphics gr, int xC, int yC, int r) {
    int x = 0, y = r, u = 1, v = 2 * r - 1, E = 0;
    while (x < y) {
      putPixel(gr, xC + x, yC + y);
      putPixel(gr, xC + y, yC - x);
      putPixel(gr, xC - x, yC - y);
      putPixel(gr, xC - y, yC + x);
      x++;
      E += u;
      u += 2;
      if (v < 2 * E) {
        y--;
        E -= v;
        v -= 2;
      }
      if (x > y)
        break;
      putPixel(gr, xC + y, yC + x);
      putPixel(gr, xC + x, yC - y);
      putPixel(gr, xC - y, yC - x);
      putPixel(gr, xC - x, yC + y);
    }
  }

  public void draw(Graphics graphics) {
    if (mInvisible)
      return;

    if (mControlPoint) {
      graphics.setColor(G.mControlPointColor);
      graphics.fillOval((int)x - ((mSize + 6)/ 2), (int)y - ((mSize + 6)/ 2), mSize + 6, mSize + 6);
      graphics.setColor(Color.darkGray);
//      graphics.setColor(mColor);
//      graphics.drawOval((int)x - ((mSize + 6)/ 2), (int)y - ((mSize + 6)/ 2), mSize + 6, mSize + 6);
//      graphics.drawOval((int)x - ((mSize + 4)/ 2), (int)y - ((mSize + 4)/ 2), mSize + 4, mSize + 4);
      drawCircle(graphics, (int)x, (int)y, (mSize + 6)/2);
      drawCircle(graphics, (int)x, (int)y, (mSize + 4)/2);
//      graphics.drawOval((int)x - (mSize / 2), (int)y - (mSize / 2), mSize, mSize);
      drawCircle(graphics, (int)x, (int)y, mSize/2);

    }
    else {
      graphics.setColor(mColor);
      graphics.fillOval((int)x - (mSize / 2), (int)y - (mSize / 2), mSize, mSize);
    }
    if (!mLabel.equals("")) {
      graphics.setFont(G.mLabelFont);
      graphics.setColor(Color.black);
      graphics.drawString(mLabel, mLabelXOff + (int)x, mLabelYOff + (int)y);
    }
  }

  public void prepareDrag(Point p) {
    mHitPoint.setLocation(p);
    mDragOffset.setLocation((int)x - mHitPoint.x, (int)y - mHitPoint.y);
    super.prepareDrag(p);
  }

  public float distance(TPoint p) {
    return (float)Math.sqrt((x - p.x)*(x - p.x) + (y - p.y)*(y - p.y));
  }

  public boolean hit(Point p) {
    if (!mSelectable)
      return false;
    int totalSize = mSize;
    if (mControlPoint)
      totalSize += 4;

    if (Util.distance(p.x, p.y, x, y) <= totalSize / 2) {
      prepareDrag(p);
      return true;
    }
    else
      return false;
  }

  public void dragged(Point p) {
    x = p.x + mDragOffset.x;
    y = p.y + mDragOffset.y;
    super.dragged(p);
  }

  public void getExtents(Rectangle extents) {
    extents.x = (int)(x - mSize / 2) - mLabelXOff;
    extents.y = (int)(y - mSize / 2) - mLabelYOff;
    extents.width = (int)(1.5 * mSize) + (2 * mLabelXOff) + LABEL_EXTENT_WIDTH;
    extents.height = (int)(1.5 * mSize) + (2 * mLabelYOff);
  }
}