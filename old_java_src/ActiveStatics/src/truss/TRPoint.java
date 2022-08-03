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

public class TRPoint extends TPoint {

  float totalMoment;
  float pDist;

  private float mDir;
  private float mDist;

  public TRPoint() {
  }

  public void update() {
/*
    if (! gCant.mPinRollerSwitch) {
      totalMoment = 0.0f;
      for (int i = 0; i < 4; i++) {
        totalMoment += gCant.mLoads[i].moment(gCant.mTrussNodes[0]);
      }
      totalMoment *= TLine.CCW(gCant.mTrussNodes[1].x + 10, gCant.mTrussNodes[1].y,
                         gCant.mTrussNodes[1].x, gCant.mTrussNodes[1].y,
                         gCant.mTrussNodes[0].x, gCant.mTrussNodes[0].y);

      float pDist = TLine.perpDist(gCant.mTrussNodes[1].x, gCant.mTrussNodes[1].y,
                                    gCant.mTrussNodes[1].x + 10, gCant.mTrussNodes[1].y,
                                    gCant.mTrussNodes[0].x, gCant.mTrussNodes[0].y);

      if (Math.abs(pDist) < 0.1f)
        pDist = 1.0f;
      totalMoment /= pDist;

      x = gCant.mTrussNodes[1].x - totalMoment;
      y = gCant.mTrussNodes[1].y;
    }
    else {
      //gCant.mRaTail.update();

      mDir = Util.direction(gCant.mLoadLine[4].x, gCant.mLoadLine[4].y,
                            gCant.mLoadLine[5].x, gCant.mLoadLine[5].y );
      mDist = Util.distance(gCant.mLoadLine[4].x, gCant.mLoadLine[4].y,
                            gCant.mLoadLine[5].x, gCant.mLoadLine[5].y );
      x = gCant.mTrussNodes[1].x - mDist * (float)Math.cos(mDir);
      y = gCant.mTrussNodes[1].y - mDist * (float)Math.sin(mDir);
    }

//    if (totalMoment < 0) {
//      gCant.mRb.mReverse = -1;
//    }
//    else {
//      gCant.mRb.mReverse = 1;
//    }


//    Util.tr("Rb: " + mLocation.x + ", " + mLocation.y);
*/
  }

  public boolean hit(Point p) {
    return false;
  }

}