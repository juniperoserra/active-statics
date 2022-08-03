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

public class TPointTranslate extends TPoint {

  public TPoint mBasePoint;
  public TPoint mVectorStart;
  public TPoint mVectorEnd;

  public TPointTranslate() {
    mControlPoint = false;
  }

  public void update() {
    x = mBasePoint.x + mVectorEnd.x - mVectorStart.x;
    y = mBasePoint.y + mVectorEnd.y - mVectorStart.y;
  }

}