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

public class TReaction extends TArrow {

  public TPoint mArrowTail = new TPoint();
  public boolean mInvisible = false;

  public TReaction() {
  }

  public void update() {
    mTrueLength = Util.distance(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y);
/*    if (mTrueLength < mArrowOffset) {
      mReverse = -1;
    }
    else if (mTrueLength == 0) {
      mReverse = 0;
    }
    else
      mReverse = 1;*/

    if (mReverse == -1) {
      tempDir = Util.direction(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y);
      mArrowTail.x = (float)(mEndPoint.x - mArrowOffset * Math.cos(tempDir));
      mArrowTail.y = (float)(mEndPoint.y - mArrowOffset * Math.sin(tempDir));

      mArrowHead.x = (float)(mStartPoint.x);
      mArrowHead.y = (float)(mStartPoint.y);
    }
    else {
      tempDir = Util.direction(mEndPoint.x, mEndPoint.y, mStartPoint.x, mStartPoint.y);
      mArrowHead.x = (float)(mEndPoint.x + mArrowOffset * Math.cos(tempDir));
      mArrowHead.y = (float)(mEndPoint.y + mArrowOffset * Math.sin(tempDir));

      mArrowTail.x = mStartPoint.x;
      mArrowTail.y = mStartPoint.y;
    }

    xHeadPoints[1] = (int)(mArrowHead.x - mSize * Math.cos(tempDir));
    yHeadPoints[1] = (int)(mArrowHead.y - mSize * Math.sin(tempDir));

    tempDir += ARROW_HEAD_SPREAD;

    xHeadPoints[0] = (int)(mArrowHead.x - mArrowHeadSize * Math.cos(tempDir));
    yHeadPoints[0] = (int)(mArrowHead.y - mArrowHeadSize * Math.sin(tempDir));

    tempDir -= 2 * ARROW_HEAD_SPREAD;

    xHeadPoints[2] = (int)(mArrowHead.x - mArrowHeadSize * Math.cos(tempDir));
    yHeadPoints[2] = (int)(mArrowHead.y - mArrowHeadSize * Math.sin(tempDir));

  }
  public float length() {
    return Util.distance(mArrowTail.x, mArrowTail.y, mArrowHead.x, mArrowHead.y);
  }

  public void draw(Graphics graphics) {
    if (mInvisible)
      return;

    graphics.setColor(mColor);
    drawThickLine(graphics, (int)mArrowTail.x, (int)mArrowTail.y,
                      (int)mArrowHead.x, (int)mArrowHead.y, mSize);
    graphics.fillPolygon(xHeadPoints, yHeadPoints, 3);
    if (!mLabel.equals("")) {
      graphics.setFont(G.mLabelFont);
      graphics.setColor(Color.black);
      if (mReverse != -1)
        graphics.drawString(mLabel, mLabelXOff + (int)((mStartPoint.x + mArrowHead.x) / 2.0f),
                                  mLabelYOff + (int)((mStartPoint.y + mArrowHead.y) / 2.0f));
      else
        graphics.drawString(mLabel, mLabelXOff + (int)((mEndPoint.x + mArrowHead.x) / 2.0f),
                                  mLabelYOff + (int)((mEndPoint.y + mArrowHead.y) / 2.0f));

    }
  }
}