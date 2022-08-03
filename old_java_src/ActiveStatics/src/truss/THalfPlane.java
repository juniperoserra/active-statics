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

public class THalfPlane extends GraphicEntity {
  public static final int DEFAULT_SIZE = 5;
  public static final int DEFAULT_DASH_LENGTH = 10;
  public static final int DEFAULT_GAP_LENGTH = 10;

  // These must be continually adjusted to conform to the enclosing component
  private int minX, maxX, minY, maxY;

  public TLine  mLine;

  public TPoint mP1;
  public TPoint mP2;
  public TPoint mSidePoint;

  //Filled polygon points
  private int xPolyPoints[] = new int[6];
  private int yPolyPoints[] = new int[6];
  private TPoint[] mCorners = new TPoint[4];
  private boolean[] mCornerIn = new boolean[4];
  private int mPointCount;

  private TPoint mLineStart;
  private TPoint mLineEnd;

  private Point mHitPoint = new Point();
  private Point mDragOffset = new Point();
  private Point tempPoint =  new Point();


  public THalfPlane() {
    mSize = DEFAULT_SIZE;
    mColor = G.mGreen;
    mLabelXOff = 0;
    mLabelYOff = -20;

    mP1 = new TPoint();
    mP2 = new TPoint();
    mSidePoint = new TPoint();

    mLineStart = new TPoint();
    mLineEnd = new TPoint();
    mLine = new TLine();
    mLine.mStartPoint = mLineStart;
    mLine.mEndPoint = mLineEnd;

    for (int i = 0; i < 4; i++) {
      mCorners[i] = new TPoint();
    }
  }

  public void setBounds(Rectangle r) {
    minX = r.x;
    maxX = minX + r.width;
    minY = r.y;
    maxY = minY + r.height;
  }

  public void draw(Graphics graphics) {
    mLine.draw(graphics);

    if (mPointCount > 2) {
      graphics.setColor(mColor);
      graphics.fillPolygon(xPolyPoints, yPolyPoints, mPointCount);
    }
  }


  public void update() {
    mLineStart.x = mP1.x;
    mLineStart.y = mP1.y;
    mLineEnd.x = mP2.x;
    mLineEnd.y = mP2.y;

    TPoint testPoint = new TPoint();

    mCorners[0].x = minX;
    mCorners[0].y = minY;
    mCorners[1].x = minX;
    mCorners[1].y = maxY;
    mCorners[2].x = maxX;
    mCorners[2].y = maxY;
    mCorners[3].x = maxX;
    mCorners[3].y = minY;

    if (mLineStart.x == mLineEnd.x) {
      mLineStart.y = minY;
      mLineEnd.y = maxY;
    }
    else if (mLineStart.y == mLineEnd.y) {
      mLineStart.x = minX;
      mLineEnd.x = maxX;
    }
    else {
      int foundPoints = 0;

      // Test against bbox top
      TLine.intersection(mP1.x, mP1.y, mP2.x, mP2.y, minX, minY, maxX, minY, testPoint);
      if (testPoint.x >= minX && testPoint.x <= maxX) {
        mLineStart.x = testPoint.x;
        mLineStart.y = testPoint.y;
        foundPoints++;
      }

      // Test against bbox left
      TLine.intersection(mP1.x, mP1.y, mP2.x, mP2.y, minX, minY, minX, maxY, testPoint);
      if (testPoint.y >= minY && testPoint.y <= maxY) {
        if (foundPoints > 0) {
          mLineEnd.x = testPoint.x;
          mLineEnd.y = testPoint.y;
        }
        else {
          mLineStart.x = testPoint.x;
          mLineStart.y = testPoint.y;
        }
        foundPoints++;
      }

      // Test against bbox right
      TLine.intersection(mP1.x, mP1.y, mP2.x, mP2.y, maxX, minY, maxX, maxY, testPoint);
      if (testPoint.y >= minY && testPoint.y <= maxY) {
        if (foundPoints > 0) {
          mLineEnd.x = testPoint.x;
          mLineEnd.y = testPoint.y;
        }
        else {
          mLineStart.x = testPoint.x;
          mLineStart.y = testPoint.y;
        }
        foundPoints++;
      }

      // Test against bbox bottom
      TLine.intersection(mP1.x, mP1.y, mP2.x, mP2.y, minX, maxY, maxX, maxY, testPoint);
      if (testPoint.x >= minX && testPoint.x <= maxX) {
        if (foundPoints > 0) {
          mLineEnd.x = testPoint.x;
          mLineEnd.y = testPoint.y;
        }
        else {
          mLineStart.x = testPoint.x;
          mLineStart.y = testPoint.y;
        }
        foundPoints++;
      }
    }

    mLine.update();

    int ccwSide = mLine.CCW(mSidePoint);
    if (ccwSide == 0) {// collinear
      mPointCount = 0;
      return;
    }

    if (ccwSide == 1) {
      xPolyPoints[0] = (int)mLine.mStartPoint.x;
      yPolyPoints[0] = (int)mLine.mStartPoint.y;
      xPolyPoints[1] = (int)mLine.mEndPoint.x;
      yPolyPoints[1] = (int)mLine.mEndPoint.y;
    }
    else {
      xPolyPoints[1] = (int)mLine.mStartPoint.x;
      yPolyPoints[1] = (int)mLine.mStartPoint.y;
      xPolyPoints[0] = (int)mLine.mEndPoint.x;
      yPolyPoints[0] = (int)mLine.mEndPoint.y;
    }
    mPointCount = 2;

    int cornersIn = 0;
    for (int i = 0; i < 4; i++) {
      mCornerIn[i] = (mLine.CCW(mCorners[i]) == ccwSide);
      if (mCornerIn[i])
        cornersIn++;
    }
    if (cornersIn == 0)
      return;

    boolean anyOut = false;
    int startCorner = 0;
    if (cornersIn == 4) {
      mPointCount = 0;
    }
    else {
      while (!anyOut || !mCornerIn[startCorner % 4]) {
        if (!mCornerIn[startCorner % 4])
          anyOut = true;
        startCorner++;
      }
    }

    for (int i = 0; i < cornersIn; i++) {
      xPolyPoints[mPointCount] = (int)mCorners[(startCorner + i) % 4].x;
      yPolyPoints[mPointCount] = (int)mCorners[(startCorner + i) % 4].y;
      mPointCount++;
    }
  }

  public boolean hit(Point p) {
    return false;
  }
}