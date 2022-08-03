package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

public class JobCirclePoint extends TimerJob {

  public TPoint mMovePoint;
  public TPoint mPivot;
  public float mSpeed;
  public G g;

  private static final float DEFAULT_SPEED = -0.02f;
  private float mTheta;
  private float mLength;
  private boolean recalc = false;

  public JobCirclePoint(G g) {
    mSpeed = DEFAULT_SPEED;
    this.g = g;
  }

  public void start() {
    mTheta = (float)Math.atan2(mMovePoint.y - mPivot.y, mMovePoint.x - mPivot.x);
    mLength = mMovePoint.distance(mPivot);
    recalc = false;
  }

  public void step() {
    if (g.selectedEntity == mPivot && g.mouseDown)
      return;
    if (g.selectedEntity == mMovePoint && g.mouseDown) {
      recalc = true;
      return;
    }
    if (recalc)
      start();

    mTheta += mSpeed;
    if (mTheta > (float)Math.PI * 2.0f)
      mTheta -= (float)Math.PI * 2.0f;
    mMovePoint.x = mPivot.x + mLength * (float)Math.cos(mTheta);
    mMovePoint.y = mPivot.y + mLength * (float)Math.sin(mTheta);
  }


}