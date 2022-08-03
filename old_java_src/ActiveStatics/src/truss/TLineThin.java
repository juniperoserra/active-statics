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

public class TLineThin extends GraphicEntity {
  public TPoint mStartPoint;
  public TPoint mEndPoint;
  public Point mLocation = new Point();

  public TLineThin() {
    mSize = 1;
    mColor = Color.darkGray;
    mLabelXOff = 0;
    mLabelYOff = -20;
  }

  public void draw(Graphics graphics) {
    graphics.setColor(mColor);

    graphics.drawLine((int)mStartPoint.x, (int)mStartPoint.y, (int)mEndPoint.x, (int)mEndPoint.y);
  }

  public void update() {
  }

  public boolean hit(Point p) {
    return false;
  }


  public float direction() {
    return Util.direction(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y);
  }

  public float length() {
    return Util.distance(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y);
  }

  public boolean intersection(TLine L2, TPoint intersection) {
    return intersection(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y,
                         L2.mStartPoint.x, L2.mStartPoint.y, L2.mEndPoint.x, L2.mEndPoint.y,
                         intersection );
  }

  public static boolean intersection(float x0,float y0,float x1,float y1,
                                  float x2,float y2,float x3,float y3,
                                  TPoint intersection)
    {
    // this function computes the intersection of the sent lines
    // and returns the intersection point, note that the function assumes
    // the lines intersect. the function can handle vertical as well
    // as horizontal lines. note the function isn't very clever, it simply
    //applies the math, but we don't need speed since this is a
    //pre-processing step

    float a1,b1,c1, // constants of linear equations
          a2,b2,c2,
          det_inv,  // the inverse of the determinant of the coefficient matrix
          m1,m2;    // the slopes of each line

    // compute slopes, note the cludge for infinity, however, this will
    // be close enough

    if ((x1-x0)!=0)
       m1 = (y1-y0)/(x1-x0);
    else
       m1 = (float)1e+10;   // close enough to infinity

    if ((x3-x2)!=0)
       m2 = (y3-y2)/(x3-x2);
    else
       m2 = (float)1e+10;   // close enough to infinity

    if (m1 == m2) {
      if (perpDist(x0, y0, x1, y1, x2, y2) == 0)  {
        intersection.x = 0;
        intersection.y = 0;
        return false;
      }
    }

    // compute constants

    a1 = m1;
    a2 = m2;

    b1 = -1;
    b2 = -1;

    c1 = (y0-m1*x0);
    c2 = (y2-m2*x2);

    // compute the inverse of the determinate

    det_inv = 1/(a1*b2 - a2*b1);

    // use Kramers rule to compute xi and yi

    intersection.x = ((b1*c2 - b2*c1)*det_inv);
    intersection.y = ((a2*c1 - a1*c2)*det_inv);
    return true;
  } // end Intersect_Lines

  public float perpDist(TPoint P) {
//    Util.tr("Perp dist: " + perpDist(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y, P.x, P.y));
//    Util.tr("  :" + mStartPoint.x + ", " + mStartPoint.y + ", " + mEndPoint.x + ", " +
//                    mEndPoint.y + ", " + P.x + ", " + P.y);
    return perpDist(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y, P.x, P.y);
  }

  public float perpDist(float PX, float PY) {
    return perpDist(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y, PX, PY);
  }

  public static float perpDist(float X1, float Y1,
                                float X2, float Y2,
                                float PX, float PY) {
    if (X1 == X2)
      return (float)Math.abs(PX - X1);
    if (Y1 == Y2)
      return (float)Math.abs(PY - Y1);

    // Adjust vectors relative to X1,Y1
    // X2,Y2 becomes relative vector from X1,Y1 to end of segment
    X2 -= X1;
    Y2 -= Y1;
    // PX,PY becomes relative vector from X1,Y1 to test point
    PX -= X1;
    PY -= Y1;
    float dotprod = PX * X2 + PY * Y2;
    // dotprod is the length of the PX,PY vector
    // projected on the X1,Y1=>X2,Y2 vector times the
    // length of the X1,Y1=>X2,Y2 vector
    float projlenSq = dotprod * dotprod / (X2 * X2 + Y2 * Y2);
    // Distance to line is now the length of the relative point
    // vector minus the length of its projection onto the line
    return (float)Math.sqrt(PX * PX + PY * PY - projlenSq);
  }

  public void closestPointOnSeg(TPoint p, TPoint closest) {
    if (mStartPoint.x == mEndPoint.x) {
      closest.x = mStartPoint.x;
      closest.y = p.y;
    }
    else if (mStartPoint.y == mEndPoint.y) {
      closest.y = mStartPoint.y;
      closest.x = p.x;
    }
    else {
      float dX = mEndPoint.x - mStartPoint.x;
      float dY = mEndPoint.y - mStartPoint.y;
      float m = - dX / dY;

      intersection(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y,
                   p.x, p.y, p.x + 10, p.y + 10 * m, closest);
    }
    float param = pointToParam(closest);
    if (param > 1.0f) param = 1.0f;
    if (param < 0.0f) param = 0.0f;
    paramToPoint(param, closest);
  }

  public void perpIntersectPoint(TPoint p, TPoint intersect) {
    if (mStartPoint.x == mEndPoint.x) {
      intersect.x = mStartPoint.x;
      intersect.y = p.y;
      return;
    }
    if (mStartPoint.y == mEndPoint.y) {
      intersect.y = mStartPoint.y;
      intersect.x = p.x;
      return;
    }

    float dX = mEndPoint.x - mStartPoint.x;
    float dY = mEndPoint.y - mStartPoint.y;
    float m = - dX / dY;

    intersection(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y,
                 p.x, p.y, p.x + 10, p.y + 10 * m, intersect);

    /*float pX = p.x - mStartPoint.x;
    float pY = p.y - mStartPoint.y;
    float dotprod = pX * dX + pY * dY;
    float projlen = dotprod / (float)Math.sqrt(dX * dX + dY * dY);
    float param = length() / projlen;
    paramToPoint(param, intersect);*/
  }

  public void paramToPoint(float t, TPoint p) {
    p.x = mStartPoint.x + (mEndPoint.x - mStartPoint.x)*t;
    p.y = mStartPoint.y + ((mEndPoint.y - mStartPoint.y)*t);
  }

  public float pointToParam(TPoint p) {
    if (mStartPoint.x == mEndPoint.x) {
      if (mStartPoint.y == mEndPoint.y)
        return 0.0f;
      return (p.y - mStartPoint.y) / (mEndPoint.y - mStartPoint.y);
    }
    else {
      return (p.x - mStartPoint.x) / (mEndPoint.x - mStartPoint.x);
    }
  }

  public int CCW(TPoint P) {
    return CCW(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y, P.x, P.y);
  }

  public int CCW(float PX, float PY) {
    return CCW(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y, PX, PY);
  }

  public static int CCW(float X1, float Y1,
                        float X2, float Y2,
                        float PX, float PY) {
    X2 -= X1;
    Y2 -= Y1;
    PX -= X1;
    PY -= Y1;
    float ccw = PX * Y2 - PY * X2;
    if (ccw == 0.0) {
      // The point is colinear, classify based on which side of
      // the segment the point falls on.  We can calculate a
      // relative value using the projection of PX,PY onto the
      // segment - a negative value indicates the point projects
      // outside of the segment in the direction of the particular
      // endpoint used as the origin for the projection.
      ccw = PX * X2 + PY * Y2;
      if (ccw > 0.0) {
        // Reverse the projection to be relative to the original X2,Y2
        // X2 and Y2 are simply negated.
        // PX and PY need to have (X2 - X1) or (Y2 - Y1) subtracted
        //    from them (based on the original values)
        // Since we really want to get a positive answer when the
        //    point is "beyond (X2,Y2)", then we want to calculate
        //    the inverse anyway - thus we leave X2 & Y2 negated.
        PX -= X2;
        PY -= Y2;
        ccw = PX * X2 + PY * Y2;
        if (ccw < 0.0) {
          ccw = 0.0f;
        }
      }
    }
    return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
  }

  public void getExtents(Rectangle extents) {
    extents.x = (int)Math.min(mStartPoint.x, mEndPoint.x);
    extents.y = (int)Math.min(mStartPoint.y, mEndPoint.y);
    extents.width = (int)Math.abs(mStartPoint.x - mEndPoint.x);
    extents.height = (int)Math.abs(mStartPoint.y - mEndPoint.y);
  }
}