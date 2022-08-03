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


public class TLoad extends TArrow {

  public G g;

  public TLoad(G aG) {
    mLabelXOff = -20;
    mLabelYOff = -14;
    mColor = Color.darkGray;
    g = aG;
  }

  public void update() {
    super.update();
    mLabel = Util.round(length() / g.mLengthDivisor, 1) + G.FORCE_UNIT;
  }

  public void draw(Graphics graphics) {
    graphics.setColor(mColor);
    drawThickLine(graphics, (int)mStartPoint.x, (int)mStartPoint.y,
                      (int)mArrowHead.x, (int)mArrowHead.y, mSize);
    graphics.fillPolygon(xHeadPoints, yHeadPoints, 3);
    if (!mLabel.equals("")) {
      graphics.setFont(g.mLabelFont);
      graphics.setColor(Color.black);
      graphics.drawString(mLabel, mLabelXOff + (int)mStartPoint.x, mLabelYOff + (int)mStartPoint.y);
    }
  }

}