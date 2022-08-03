package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

public class CascadedJobMovePointTo extends TimerJob {

  public TPoint mMovePoint;
  public float xDst, yDst;
  private CascadedJobMovePointTo previousJob;

  private static final float moveProportion = 0.1f;
  private float dx, dy;
  private boolean xDone = false;
  private boolean yDone = false;
  private boolean mDone = false;

  public G g;

  public CascadedJobMovePointTo(CascadedJobMovePointTo previousJob, G aG) {
    this.previousJob = previousJob;
    g = aG;
  }

  public void step() {
    if (previousJob != null && !previousJob.mDone)  // Must wait for the last job to finish
      return;

    g.autoMove = true;

    if (!xDone) {
      dx = mMovePoint.x - xDst;
      if (Math.abs(dx) <= 1.0) {
        mMovePoint.x = xDst;
        xDone = true;
      }
      else {
        mMovePoint.x -= dx * moveProportion;
      }
    }

    if (!yDone) {
      dy = mMovePoint.y - yDst;
      if (Math.abs(dy) <= 1.0) {
        mMovePoint.y = yDst;
        yDone = true;
      }
      else {
        mMovePoint.y -= dy * moveProportion;
      }
    }

    if (xDone && yDone) {
      mDone = true;
      g.autoMove = false;
      g.mTimer.removeJob(this);
    }
  }
}