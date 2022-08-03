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

public class TArrow extends TLine {

  public int ARROW_OFFSET = 15;
  public static final float ARROW_HEAD_SPREAD = 15; //(float)(15 * Math.PI / 180.0);
  public static final int ARROW_HEAD_SIZE = 12;
  public int mArrowOffset;
  public float mArrowHeadSpread;
  public int mArrowHeadSize;
  public TPoint mArrowHead;
  int xHeadPoints[] = new int[3];
  int yHeadPoints[] = new int[3];
  int mReverse = 1;
  float mTrueLength;

  protected float tempDir;

  public TArrow() {
    mArrowOffset = ARROW_OFFSET;
    mArrowHeadSpread = ARROW_HEAD_SPREAD;
    mArrowHeadSize = ARROW_HEAD_SIZE;
    mArrowHead = new TPoint();
  }

  public void update() {
    mTrueLength = Util.distance(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y);
    if (mTrueLength < mArrowOffset) {
      mReverse = -1;
    }
    else if (mTrueLength == 0) {
      mReverse = 0;
    }
    else
      mReverse = 1;

    tempDir = Util.direction(mEndPoint.x, mEndPoint.y, mStartPoint.x, mStartPoint.y);
    mArrowHead.x = (float)(mEndPoint.x + mArrowOffset * Math.cos(tempDir));
    mArrowHead.y = (float)(mEndPoint.y + mArrowOffset * Math.sin(tempDir));

    xHeadPoints[1] = (int)(mArrowHead.x - (mReverse * mSize) * Math.cos(tempDir));
    yHeadPoints[1] = (int)(mArrowHead.y - (mReverse * mSize) * Math.sin(tempDir));

    tempDir += ARROW_HEAD_SPREAD;

    xHeadPoints[0] = (int)(mArrowHead.x - mReverse * mArrowHeadSize * Math.cos(tempDir));
    yHeadPoints[0] = (int)(mArrowHead.y - mReverse * mArrowHeadSize * Math.sin(tempDir));

    tempDir -= 2 * ARROW_HEAD_SPREAD;

    xHeadPoints[2] = (int)(mArrowHead.x - mReverse * mArrowHeadSize * Math.cos(tempDir));
    yHeadPoints[2] = (int)(mArrowHead.y - mReverse * mArrowHeadSize * Math.sin(tempDir));
  }

  public void drawDashedLine(Graphics g, int x1, int y1, int x2, int y2, int thickness) {
   float fromX, toX, fromY, toY;
   float dist = Util.distance(x1, y1, x2, y2);
   float dir = Util.direction(x1, y1, x2, y2);
   float fromDist = 0.0f;
   float toDist = 0.0f;
   float cos = (float)Math.cos(dir);
   float sin = (float)Math.sin(dir);

   while (fromDist < dist) {
     toDist = (float)Math.min(fromDist + mDashLength, dist);
     fromX = x1 + fromDist * cos;
     fromY = y1 + fromDist * sin;
     toX = x1 + toDist * cos;
     toY = y1 + toDist * sin;

     drawThickLine(g, (int)fromX, (int)fromY, (int)toX, (int)toY, thickness);

     fromDist = toDist + mGapLength;
   }

  }


  public void draw(Graphics graphics) {
    if (mInvisible)
      return;

    graphics.setColor(mColor);
    if (!mDashed) {
      drawThickLine(graphics, (int) mStartPoint.x, (int) mStartPoint.y,
                    (int) mArrowHead.x, (int) mArrowHead.y, mSize);
    }
    else {
      drawDashedLine(graphics, (int) mStartPoint.x, (int) mStartPoint.y,
                    (int) mArrowHead.x, (int) mArrowHead.y, mSize);
    }
    graphics.fillPolygon(xHeadPoints, yHeadPoints, 3);
    if (!mLabel.equals("")) {
      graphics.setFont(G.mLabelFont);
      graphics.setColor(Color.black);
      graphics.drawString(mLabel, mLabelXOff + (int)((mStartPoint.x + mArrowHead.x) / 2.0f),
                                  mLabelYOff + (int)((mStartPoint.y + mArrowHead.y) / 2.0f));
    }
  }

  public boolean hit(Point p) {
    return false;
  }

  public float length() {
    return Util.distance(mStartPoint.x, mStartPoint.y, mArrowHead.x, mArrowHead.y);
  }

  public float moment(TPoint pivot) {
    return  CCW(mStartPoint.x, mStartPoint.y, mArrowHead.x, mArrowHead.y, pivot.x, pivot.y) *
            length() *
            perpDist(pivot);
  }
}