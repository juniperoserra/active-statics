package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

public class JobMovePointTo extends TimerJob {

  public TPoint mMovePoint;
  public float xDst, yDst;
  public boolean forceSelectMovingNode = false;

  private static final float moveProportion = 0.1f;
  private float dx, dy;
  private boolean xDone = false;
  private boolean yDone = false;

  public JobMovePointTo(G g) {
    super(g);
  }

  public JobMovePointTo(TimerJob afterJob, G g) {
    super(afterJob, g);
  }

  public void step() {
    if (afterJob != null && !afterJob.done)
      return;

    if (g.selectedEntity == mMovePoint && g.mouseDown)
      return;

    if (forceSelectMovingNode || (g.selectedEntity == null || !g.selectedEntity.isButton()))
     g.selectedEntity = mMovePoint;

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
      done = true;
    }
  }
}