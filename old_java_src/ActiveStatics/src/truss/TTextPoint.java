package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

public class TTextPoint extends TText {

  public TPoint mBasePoint;
  public int mXOffset;
  public int mYOffset;

  public TTextPoint() {
  }

  public void update() {
    super.update();
    x = mBasePoint.x + mXOffset;
    y = mBasePoint.y + mYOffset;
  }
}