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

// This implements an arrow that reflects itself into a single halfplane denoted by the mSide angle.
// mAnchorPoint will always correspond to one of the start or end points, but which one it is will switch

public class TArrowOneSide extends TArrow {

  public TPoint mAnchorPoint;
  public TPoint mLabelPoint = new TPoint();
  public float  mSide;          // Angle (0 - 360) in which the arrow can appear
  public float xOff, yOff;

  public TArrowOneSide() {
  }

  public void update() {
    updateNoReverse();

    if (sameSide(0.0f, (float)(Util.direction(mStartPoint.x, mStartPoint.y, mAnchorPoint.x, mAnchorPoint.y) * 180 / Math.PI))) {
      //System.out.println("Same side " + mLabel);
      mLabelPoint.x = mStartPoint.x;
      mLabelPoint.y = mStartPoint.y;
      return;
    }

    tempDir = (float)direction();
    float dx = (mAnchorPoint.x - mStartPoint.x);
    float dy = (mAnchorPoint.y - mStartPoint.y);

    mStartPoint.x += dx;
    mStartPoint.y += dy;
    mEndPoint.x += dx;
    mEndPoint.y += dy;


    mLabelPoint.x = mEndPoint.x;
    mLabelPoint.y = mEndPoint.y;

    updateNoReverse();
  }

  private void updateNoReverse() {
    mTrueLength = Util.distance(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y);
    mReverse = 1;

    tempDir = Util.direction(mEndPoint.x, mEndPoint.y, mStartPoint.x, mStartPoint.y);
    mArrowHead.x = mEndPoint.x;
    mArrowHead.y = mEndPoint.y;

    float dir;
    if (mStartPoint.x == mAnchorPoint.x && mStartPoint.y == mAnchorPoint.y) {
      dir = Util.direction(mAnchorPoint.x, mAnchorPoint.y, mEndPoint.x, mEndPoint.y );
    }
    else {
      dir = Util.direction(mAnchorPoint.x, mAnchorPoint.y, mStartPoint.x, mStartPoint.y );
    }
    xOff = mArrowOffset * (float)Math.cos(dir);
    yOff = mArrowOffset * (float)Math.sin(dir);

    xHeadPoints[1] = (int)(mArrowHead.x - (mReverse * mSize) * Math.cos(tempDir) + xOff);
    yHeadPoints[1] = (int)(mArrowHead.y - (mReverse * mSize) * Math.sin(tempDir) + yOff);

    tempDir += ARROW_HEAD_SPREAD;

    xHeadPoints[0] = (int)(mArrowHead.x - mReverse * mArrowHeadSize * Math.cos(tempDir) + xOff);
    yHeadPoints[0] = (int)(mArrowHead.y - mReverse * mArrowHeadSize * Math.sin(tempDir) + yOff);

    tempDir -= 2 * ARROW_HEAD_SPREAD;

    xHeadPoints[2] = (int)(mArrowHead.x - mReverse * mArrowHeadSize * Math.cos(tempDir) + xOff);
    yHeadPoints[2] = (int)(mArrowHead.y - mReverse * mArrowHeadSize * Math.sin(tempDir) + yOff);
  }


  //Unlike the other arrows, the offset is done to these on drawing

  public void draw(Graphics graphics) {
    graphics.setColor(mColor);
    drawThickLine(graphics, (int)(mStartPoint.x + xOff), (int)(mStartPoint.y + yOff),
                      (int)(mArrowHead.x + xOff), (int)(mArrowHead.y + yOff), mSize);
    graphics.fillPolygon(xHeadPoints, yHeadPoints, 3);
    if (!mLabel.equals("")) {
      graphics.setFont(G.mLabelFont);
      graphics.setColor(Color.black);
      graphics.drawString(mLabel, mLabelXOff + (int)((mStartPoint.x + mArrowHead.x) / 2.0f),
                                  mLabelYOff + (int)((mStartPoint.y + mArrowHead.y) / 2.0f));
    }
  }

  private boolean sameSide(float aRef, float a) {    // aRef, a are 0 - 360.0
    float aR, aT;
    aR = aRef;
    aT = a;

    if (aR <= 90.0f) {
      aT += 90.0f;
      aR += 90.0f;
    }
    if (aR >= 270.0f) {
      aT -= 90.0f;
      aR -= 90.0f;
    }
    if (aT >= 360.0f)
      aT -= 360.0f;
    if (aT < 0.0f)
      aT += 360.0f;

    if (aT > aR + 90.0f)
      return false;
    if (aT < aR - 90.0f)
      return false;
    return true;

  }

}