package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

 // Moves a point to its start point when the previous job (of the same type) is done

public class CascadedJobMovePointToStart extends TimerJob {

  public TPoint mMovePoint;

  public boolean mDone;

  private static final float moveProportion = 0.1f;
  private float dx, dy;
  private boolean xDone = false;
  private boolean yDone = false;
  private CascadedJobMovePointToStart previousJob;

  public G g;

  public CascadedJobMovePointToStart(CascadedJobMovePointToStart previousJob, G aG) {
    this.previousJob = previousJob;
    mDone = false;
    g = aG;
  }

  public void step() {
    if (previousJob != null && !previousJob.mDone)  // Must wait for the last job to finish
      return;

    g.autoMove = true;
    g.selectedEntity = mMovePoint;

    if (!xDone) {
      dx = mMovePoint.x - mMovePoint.xStart;
      if (Math.abs(dx) <= 1.0) {
        mMovePoint.x = mMovePoint.xStart;
        xDone = true;
      }
      else {
        mMovePoint.x -= dx * moveProportion;
      }
    }

    if (!yDone) {
      dy = mMovePoint.y - mMovePoint.yStart;
      if (Math.abs(dy) <= 1.0) {
        mMovePoint.y = mMovePoint.yStart;
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