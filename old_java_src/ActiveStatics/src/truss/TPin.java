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

public class TPin extends GraphicEntity {

  public static final int DEFAULT_SIZE = 22;
  public static final int BASE_EXTEND = 8;
  public static final int OFFSET = 8;
  public static final int N_GROUND_LINES = 8;
  public static final int GROUND_LINE_LENGTH = 8;

  public float x, y;
  private float x2, y2, x3, y3, dx, dy;
  private float mRadDir, mRadDir1, mRadDir2, mRadDir3;
  public float mDir = 270.0f;
  public TPoint mPoint;
  private TLine mLine = new TLine();

/*  public boolean mControlPoint = true;
  public float xStart, yStart;
  private Point mHitPoint = new Point();
  private Point mDragOffset = new Point();
  private Point tempPoint =  new Point();*/

  public TPin() {
    mSize = DEFAULT_SIZE;
  }

  public TPin(TPoint aPoint) {
    mSize = DEFAULT_SIZE;
    mPoint = aPoint;
  }

  public void draw(Graphics graphics) {
    mRadDir = (float)((mDir) * Math.PI / 180.0f);
    x = (float)(mPoint.x - OFFSET * Math.cos(mRadDir));
    y = (float)(mPoint.y - OFFSET * Math.sin(mRadDir));
    mRadDir1 = (float)((mDir - 30) * Math.PI / 180.0f);
    mRadDir2 = (float)((mDir + 30) * Math.PI / 180.0f);
    mRadDir3 = (float)((mDir + 90) * Math.PI / 180.0f);
    x2 = (float)(x - mSize * Math.cos(mRadDir1));
    y2 = (float)(y - mSize * Math.sin(mRadDir1));
    x3 = (float)(x - mSize * Math.cos(mRadDir2));
    y3 = (float)(y - mSize * Math.sin(mRadDir2));
    graphics.setColor(Color.darkGray);
    mLine.drawThickLine(graphics, (int)x, (int)y, (int)x2, (int)y2, 2);
    mLine.drawThickLine(graphics, (int)x, (int)y, (int)x3, (int)y3, 2);

    x2 += (float)(BASE_EXTEND * Math.cos(mRadDir3));
    y2 += (float)(BASE_EXTEND * Math.sin(mRadDir3));
    x3 -= (float)(BASE_EXTEND * Math.cos(mRadDir3));
    y3 -= (float)(BASE_EXTEND * Math.sin(mRadDir3));

    mLine.drawThickLine(graphics, (int)x2, (int)y2, (int)x3, (int)y3, 2);
    mRadDir1 = (float)((mDir - 150) * Math.PI / 180.0f);
    dx = (float)(GROUND_LINE_LENGTH * Math.cos(mRadDir1));
    dy = (float)(GROUND_LINE_LENGTH * Math.sin(mRadDir1));

    graphics.setColor(Color.black);
    for (int i = 1; i < N_GROUND_LINES; i++) {
      x = x2 + (((x3 - x2)/(float)(N_GROUND_LINES * 2))*(2 * i - 1));
      y = y2 + (((y3 - y2)/(float)(N_GROUND_LINES * 2))*(2 * i - 1));
      //mLine.drawThickLine(graphics, (int)x, (int)y, (int)(x + dx), (int)(y + dy), 2);
      graphics.drawLine((int)x, (int)y, (int)(x + dx), (int)(y + dy));
    }


/*    if (mControlPoint) {
      graphics.setColor(g.mControlPointColor);
      graphics.fillOval((int)x - ((mSize + 6)/ 2), (int)y - ((mSize + 6)/ 2), mSize + 6, mSize + 6);
      graphics.setColor(Color.black);
      graphics.drawOval((int)x - ((mSize + 6)/ 2), (int)y - ((mSize + 6)/ 2), mSize + 6, mSize + 6);
      graphics.drawOval((int)x - ((mSize + 4)/ 2), (int)y - ((mSize + 4)/ 2), mSize + 4, mSize + 4);
      graphics.setColor(mColor);
      graphics.drawOval((int)x - (mSize / 2), (int)y - (mSize / 2), mSize, mSize);

    }
    else {
      graphics.setColor(mColor);
      graphics.fillOval((int)x - (mSize / 2), (int)y - (mSize / 2), mSize, mSize);
    }
    if (!mLabel.equals("")) {
      graphics.setFont(g.mLabelFont);
      graphics.setColor(Color.black);
      graphics.drawString(mLabel, mLabelXOff + (int)x, mLabelYOff + (int)y);
    }*/
  }

}