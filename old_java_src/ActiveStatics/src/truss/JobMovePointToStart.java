package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

public class JobMovePointToStart extends TimerJob {

  public TPoint mMovePoint;

  private static final float moveProportion = 0.1f;
  private float dx, dy;
  private boolean xDone = false;
  private boolean yDone = false;
  public boolean forceSelectMovingNode = false;

  public JobMovePointToStart(G g) {
    super(g);
  }

  public JobMovePointToStart(TimerJob afterJob, G g) {
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

//    System.out.println("Dx, Dy: " + dx + ", " + dy);

    if (xDone && yDone) {
//      System.out.println("Move point to start done.");
      done = true;
    }
  }
}