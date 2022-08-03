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

public class TRoller extends GraphicEntity {


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

  public TRoller() {
    mSize = DEFAULT_SIZE;
  }

  public TRoller(TPoint aPoint) {
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
    x2 = (float)(x - (mSize / 2) * Math.cos(mRadDir1));
    y2 = (float)(y - (mSize / 2) * Math.sin(mRadDir1));
    x3 = (float)(x - (mSize / 2) * Math.cos(mRadDir2));
    y3 = (float)(y - (mSize / 2) * Math.sin(mRadDir2));
    graphics.setColor(Color.darkGray);
    mLine.drawThickLine(graphics, (int)x, (int)y, (int)x2, (int)y2, 2);
    mLine.drawThickLine(graphics, (int)x, (int)y, (int)x3, (int)y3, 2);
    mLine.drawThickLine(graphics, (int)x2, (int)y2, (int)x3, (int)y3, 2);

    if (mDir == 0.0f)
      dx = (float)((mSize / 2) * Math.cos(mDir));
    else
      dx = (float)((mSize / 4) * Math.cos(mRadDir3));
    dy = -(float)((mSize / 4) * Math.sin(mRadDir3));

    graphics.fillOval((int)(x2 - dx), (int)(y2 + dy), mSize / 2, mSize / 2);
    graphics.fillOval((int)(x3 - dx), (int)(y3 + dy), mSize / 2, mSize / 2);

    graphics.setColor(G.mBackground);
    graphics.fillOval((int)(x2 - dx) + 2, (int)(y2 + dy) + 2, mSize / 2 - 4, mSize / 2 - 4);
    graphics.fillOval((int)(x3 - dx) + 2, (int)(y3 + dy) + 2, mSize / 2 - 4, mSize / 2 - 4);

    graphics.setColor(Color.darkGray);

    x2 = (float)(x - mSize * Math.cos(mRadDir1));
    y2 = (float)(y - mSize * Math.sin(mRadDir1));
    x3 = (float)(x - mSize * Math.cos(mRadDir2));
    y3 = (float)(y - mSize * Math.sin(mRadDir2));

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