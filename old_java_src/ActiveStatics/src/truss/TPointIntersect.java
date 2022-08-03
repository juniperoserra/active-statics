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

public class TPointIntersect extends TPoint {

  public static final double MAX_COORD = 5000.0;
  public static final double MIN_COORD = -5000.0;

  public TLine mL1;
  public TLine mL2;
  public boolean mExists;

  TPoint tmpPoint = new TPoint();

  public TPointIntersect() {
    mControlPoint = false;
  }

  public TPointIntersect(TLine l1, TLine l2) {
    mL1 = l1;
    mL2 = l2;
    mControlPoint = false;
  }

  public void update() {
    mExists = mL1.intersection(mL2, tmpPoint);
    if (tmpPoint.x == Float.NEGATIVE_INFINITY || tmpPoint.x == Float.POSITIVE_INFINITY || tmpPoint.x == Float.NaN)
      mExists = false;
     if (tmpPoint.y == Float.NEGATIVE_INFINITY || tmpPoint.y == Float.POSITIVE_INFINITY || tmpPoint.y == Float.NaN)
      mExists = false;

    if (mExists) {
      x = (float)Util.clamp(tmpPoint.x, MIN_COORD, MAX_COORD);
      y = (float)Util.clamp(tmpPoint.y, MIN_COORD, MAX_COORD);
      return;
    }
  }
}