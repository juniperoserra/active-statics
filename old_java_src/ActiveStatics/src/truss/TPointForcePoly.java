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

public class TPointForcePoly extends TPoint {

  public TPoint mMember1ForceBegin;
  public TLine mMember1;
  private float mMember1Dir;

  public TPoint mMember2ForceBegin;
  public TLine mMember2;
  private float mMember2Dir;

  float x1, y1, x2, y2, x3, y3, x4, y4;

  public TPointForcePoly() {
    mSize = 7;
    mControlPoint = false;
  }

  public void update() {
    x1 = mMember1ForceBegin.x;
    y1 = mMember1ForceBegin.y;
    mMember1Dir = mMember1.direction();
    x2 = x1 + 10.0f * (float)Math.cos(mMember1Dir);
    y2 = y1 + 10.0f * (float)Math.sin(mMember1Dir);

    x3 = mMember2ForceBegin.x;
    y3 = mMember2ForceBegin.y;
    mMember2Dir = mMember2.direction();
    x4 = x3 + 10.0f * (float)Math.cos(mMember2Dir);
    y4 = y3 + 10.0f * (float)Math.sin(mMember2Dir);

    if (x1 == x2 && x3 == x4 && x1 == x3) {   // Very special case
      x = x3;
      y = y3;
    }
    else
      TLine.intersection(x1, y1, x2, y2, x3, y3, x4, y4, this);
  }

}