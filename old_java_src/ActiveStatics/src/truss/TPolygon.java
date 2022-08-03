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

public class TPolygon extends GraphicEntity {

  private int mXPolyPoints[];
  private int mYPolyPoints[];
  private TPoint mPolyPoints[];
  public int mNPoints = 0;

  public boolean mOutline = false;
  public Color   mOutlineColor = Color.darkGray;

  public TPolygon() {
  }

  public void addPoint(TPoint point) {
    mNPoints++;
    TPoint tempPoints[] = new TPoint[mNPoints];
    for (int i = 0; i < mNPoints - 1; i++) {
      tempPoints[i] = mPolyPoints[i];
    }
    tempPoints[mNPoints - 1] = point;
    mYPolyPoints = new int[mNPoints];
    mXPolyPoints = new int[mNPoints];
    mPolyPoints = tempPoints;
  }

  public void update() {
    if (mNPoints <= 2)
      return;

    for (int i = 0; i < mNPoints; i++) {
      mXPolyPoints[i] = (int)mPolyPoints[i].x;
      mYPolyPoints[i] = (int)mPolyPoints[i].y;
    }
  }

  public void draw(Graphics graphics) {
    if (mNPoints <= 2)
      return;

    graphics.setColor(mColor);
    graphics.fillPolygon(mXPolyPoints, mYPolyPoints, mNPoints);

    if (mOutline) {
      graphics.setColor(mOutlineColor);
      for(int i = 0; i < mNPoints; i++) {
        graphics.drawLine(mXPolyPoints[i], mYPolyPoints[i], mXPolyPoints[(i+1) % mNPoints], mYPolyPoints[(i+1) % mNPoints]);
      }
    }
    //System.out.println("Here!");
  }

  public void getExtents(Rectangle extents) {
    if (mNPoints <= 2) {
      super.getExtents(extents);
      return;
    }

    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;

    for (int i = 0; i < mNPoints; i++) {
      if (mXPolyPoints[i] < minX) minX = mXPolyPoints[i];
      if (mXPolyPoints[i] > maxX) maxX = mXPolyPoints[i];
      if (mYPolyPoints[i] < minY) minY = mYPolyPoints[i];
      if (mYPolyPoints[i] > maxY) maxY = mYPolyPoints[i];
    }
    extents.x = minX;
    extents.y = minY;
    extents.width = maxX - minX;
    extents.height = maxY - minY;
  }
}